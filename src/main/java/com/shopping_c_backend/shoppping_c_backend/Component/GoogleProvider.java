package com.shopping_c_backend.shoppping_c_backend.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shopping_c_backend.shoppping_c_backend.Constants.GoogleConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.GoogleUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ProviderToken;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GoogleProvider {
    /**
     * 根据用户授权信息获取当前用户的 accessToken
     * 对用户进行授权，先创建一个GET请求，请求gitee中对应用户的访问令牌
     *
     * @param providerToken 用户授权信息
     * @return 当前用户的 accessToken
     */
    private final GoogleConstants googleConstants;
    Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10808));

    @Autowired
    public GoogleProvider(GoogleConstants googleConstants) {
        this.googleConstants = googleConstants;
    }

    public String getGoogleToken(ProviderToken providerToken) {
        //1. 创建http请求，构建请求体和请求url等，并向gitee发起请求
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .dns(hostname -> {
                    List<InetAddress> addresses = new ArrayList<>();
                    addresses.add(InetAddress.getByName("8.8.8.8"));  // 使用 Google DNS // 可能是这个有问题
                    return addresses;
                })
                .build();
        MediaType mediaType = MediaType.get("application/x-www-form-urlencoded; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, "code=" + providerToken.getCode() +
                "&client_id=" + googleConstants.getAppID() +
                "&client_secret=" + googleConstants.getAppKEY() +
                "&redirect_uri=" + googleConstants.getRedirectURI() +
                "&grant_type=authorization_code");
        Request request = new Request.Builder()
                .url(googleConstants.getAccessToken())
                .post(body)
                .build();

        //2. 获取gitee对应的响应消息，根据消息解析出用户的 access token
        try (Response response = client.newCall(request).execute()) {
            String tokenStr = Objects.requireNonNull(response.body()).string();
            String accessToken = null;
            JSONObject jsonObject = JSON.parseObject(tokenStr);
            accessToken = jsonObject.getString("access_token");
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整的异常堆栈信息，帮助调试
            System.err.println("Error occurred while fetching Google access token.");
            return null;
        }
    }

    /**
     * 根据用户的 access token 获取当前gitee用户的详细信息
     *
     * @param accessToken 用户的访问令牌
     * @return gitee用户对象
     */
    public GoogleUserEntity getGoogleUser(String accessToken) {
        //1. 构建http的GET请求，向gitee请求用户数据
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .dns(hostname -> {
                    List<InetAddress> addresses = new ArrayList<>();
                    addresses.add(InetAddress.getByName("8.8.8.8"));  // 使用 Google DNS // 可能是这个有问题
                    return addresses;
                })
                .build();
        Request request = new Request.Builder()
                .url(googleConstants.getUserInfo() + "?access_token=" + accessToken).build();

        //2. 获取gitee传回来的响应消息，根据消息解析出用户消息
        try {
            Response response = client.newCall(request).execute();
            String googleUserStr = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = JSON.parseObject(googleUserStr);
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            GoogleUserEntity googleUserEntity = new GoogleUserEntity();
            googleUserEntity.setId(id);
            googleUserEntity.setName(name);
            return googleUserEntity;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }
}

