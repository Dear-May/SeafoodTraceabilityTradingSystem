package com.shopping_c_backend.shoppping_c_backend.Controller;

import com.shopping_c_backend.shoppping_c_backend.Util.NetworkUtil;
import com.shopping_c_backend.shoppping_c_backend.Util.WeatherUtil;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/other")
public class OtherController {
    @Resource
    private WeatherUtil weatherUtil;
    PolicyFactory policy = new HtmlPolicyBuilder()
            .allowElements("b", "i", "em", "strong", "a") // 允许的HTML元素
            .allowUrlProtocols("http", "https") // 允许的URL协议
            .toFactory();

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/weather", method = RequestMethod.POST, headers = "Content-Type=application/json")
    public Map<String, Object> getWeather(@RequestBody Map<String, String> requestMap) {
        String city = policy.sanitize(requestMap.get("city"));
        return weatherUtil.getWeather(city);
    }

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "/getHostIp", method = RequestMethod.GET)
    public String getHostIp() {
        return NetworkUtil.getLocalIpAddress();
    }
}
