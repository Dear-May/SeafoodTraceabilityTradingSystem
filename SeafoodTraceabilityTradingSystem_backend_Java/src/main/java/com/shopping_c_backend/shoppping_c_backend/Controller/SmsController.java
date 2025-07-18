package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.utils.StringUtils;
import com.shopping_c_backend.shoppping_c_backend.Service.RedisService;
import com.shopping_c_backend.shoppping_c_backend.Service.UserServiceImpl;
import com.shopping_c_backend.shoppping_c_backend.Util.SmsCodeGenerateUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.SmsUtil;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/sms")
public class SmsController {


    /***
     * 注入redis模版
     */
    @Resource
    private RedisService redisService;
    private String tokenId = "TOKEN-USER-";

    @Resource
    @Lazy
    private UserServiceImpl userService;
    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    /**
     * 发送短信
     *
     * @throws ClientException 抛出异常
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     */
    @ResponseBody
    @RequestMapping(value = "/smsXxs", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> smsXxs(@RequestBody Map<String, Object> requestMap, HttpServletRequest request) throws ClientException {
        Map<String, Object> map = new HashMap<>();
        String phone = policy.sanitize(requestMap.get("phoneNumber").toString());//获取注册手机号码
        // 调用工具栏中生成验证码方法（指定长度的随机数）
        String code = SmsCodeGenerateUtil.generateVerifyCode(6);
        //填充验证码
        String TemplateParam = "{\"code\":\"" + code + "\"}";
        SendSmsResponse response = SmsUtil.sendSms(phone, TemplateParam);//传入手机号码及短信模板中的验证码占位符

        map.put("verifyCode", code);
        map.put("phone", phone);
        request.getSession().setAttribute("CodePhone", map);
        if (response.getCode().equals("OK")) {
            map.put("isOk", "OK");
            //验证码绑定手机号并存储到redis
            redisService.set(tokenId + phone, code);
            redisService.expire(tokenId + phone, 620);//调用reids工具类中存储方法设置超时时间
        }
        return map;
    }


    /**
     * 注册验证
     *
     * @throws ClientException 抛出异常
     * @ResponseBody 返回json数据
     * @RequestMapping 拦截请求，指定请求类型：POST
     * @RequestBody 接受前台传入的json数据 接受类型为Map
     */
    @ResponseBody
    @RequestMapping(value = "/validateNum", method = RequestMethod.POST, headers = "Accept=application/json")
    public Map<String, Object> validateNum(@RequestBody Map<String, Object> requestMap) throws ClientException {

        Map<String, Object> map = new HashMap<>();
        String phone = policy.sanitize(requestMap.get("phone").toString());
        String verifyCode = policy.sanitize(requestMap.get("verifyCode").toString()); //获取手机验证码

        //首先比对验证码是否失效
        String redisauthcode = redisService.get(tokenId + phone); //传入tonkenId返回redis中的value
        if (StringUtils.isEmpty(redisauthcode)) {
            //如果未取到则过期验证码已失效
            map.put("result", 404);
        } else {
            //验证码正确
            if (redisauthcode.equals(verifyCode)) {
                if (userService.selectPhone(phone)) {
                    map.put("result", 200);
                } else {
                    map.put("result", 403);
                }
            } else {
                //验证码错误
                System.out.println("验证码错误");
                map.put("result", 500);
            }
        }
        return map;
    }


}
