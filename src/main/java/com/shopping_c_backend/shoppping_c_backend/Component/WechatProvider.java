package com.shopping_c_backend.shoppping_c_backend.Component;

import com.alibaba.fastjson.JSON;
import com.shopping_c_backend.shoppping_c_backend.Constants.WechatConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.WechatUserEntity;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class WechatProvider {
    private final WechatConstants wechatConstants;

    @Autowired
    public WechatProvider(WechatConstants wechatConstants) {
        this.wechatConstants = wechatConstants;
    }

    public String getWechatToken() {
        String url = wechatConstants.getAccessToken();
        String appId = wechatConstants.getAppID();
        String appSecret = wechatConstants.getAppKEY();
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("grant_type", "client_credential")
                .add("appid", appId)
                .add("secret", appSecret)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = Objects.requireNonNull(response.body()).string();
                String accessToken = JSON.parseObject(responseBody).getString("access_token");
                return accessToken;
            } else {
                throw new RuntimeException("Failed to get wechat access token");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get wechat access token");
        }
    }

    public WechatUserEntity getWechatUser(String id) {
        WechatUserEntity wechatUserEntity = new WechatUserEntity();
        wechatUserEntity.setId(id);
        wechatUserEntity.setName("微信用户");
        return wechatUserEntity;
    }
}
