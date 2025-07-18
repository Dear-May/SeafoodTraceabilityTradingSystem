package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.aliyuncs.utils.StringUtils;
import com.shopping_c_backend.shoppping_c_backend.Service.RedisService;
import com.shopping_c_backend.shoppping_c_backend.Util.EmailUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.VerCodeGenerateUtil;
import com.shopping_c_backend.shoppping_c_backend.Vo.Result;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/email")
public class EmailController {
    /***
     * 注入redis模版
     */
    @Resource
    private RedisService redisService;
    private String tokenId = "TOKEN-EMAIL-";
    @Resource
    private EmailUtil emailUtil;
    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result sendEmail(@RequestBody Map<String, Object> requestMap) {
        String email = requestMap.get("email").toString();
        String verCode = VerCodeGenerateUtil.getVerCode();
        String subject = "【海潮】 注册账号验证码";
        if (emailUtil.sendEmail(email, verCode, subject)) {
            //验证码绑定手机号并存储到redis
            redisService.set(tokenId + email, verCode);
            redisService.expire(tokenId + email, 60 * 5);//调用reids工具类中存储方法设置超时时间
        } else {
            return new Result(500);
        }
        return new Result(200);
    }

    @ResponseBody
    @RequestMapping(value = "/validateNum", method = RequestMethod.POST, headers = "Accept=application/json")
    public Result validateNum(@RequestBody Map<String, Object> requestMap) {
        String email = requestMap.get("email").toString();
        String verCode = policy.sanitize(requestMap.get("verCode").toString());

        //首先比对验证码是否失效
        String redisauthcode = redisService.get(tokenId + email); //传入tonkenId返回redis中的value
        if (StringUtils.isEmpty(redisauthcode)) {
            //如果未取到则过期验证码已失效
            return new Result(400);
        } else {
            //验证码正确
            if (redisauthcode.equals(verCode)) {
                return new Result(200);
            } else {
                //验证码错误
                System.out.println("验证码错误");
                return new Result(500);
            }
        }
    }

}
