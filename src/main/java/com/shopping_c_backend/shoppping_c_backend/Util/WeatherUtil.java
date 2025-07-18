package com.shopping_c_backend.shoppping_c_backend.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shopping_c_backend.shoppping_c_backend.Constants.WeatherConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Component
public class WeatherUtil {
    private final WeatherConstants weatherConstants;
    private static final Logger logger = LoggerFactory.getLogger(WeatherUtil.class);

    public WeatherUtil(WeatherConstants weatherConstants) {
        this.weatherConstants = weatherConstants;
    }

    public Map<String, Object> getWeather(String city) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            // 请求 URL
            if (city.endsWith("市")) {
                city = city.substring(0, city.length() - 1);
            }
            String urlString = "http://gfeljm.tianqiapi.com/api?unescape=1&version=v63&appid=" + weatherConstants.getAppID() + "&appsecret=" + weatherConstants.getAppKEY() + "&city=" + city;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            // 关闭连接
            in.close();
            conn.disconnect();
            // 使用 fastjson 解析 JSON
            JSONObject json = JSON.parseObject(content.toString());
            String weather = json.getString("wea");  // 提取天气情况
            String temperature = json.getString("tem");  // 提取当前温度
            result.put("weather", weather);
            result.put("temperature", temperature);
            return result;

        } catch (Exception e) {
            logger.error("发生异常：", e);
        }
        return result;
    }
}
