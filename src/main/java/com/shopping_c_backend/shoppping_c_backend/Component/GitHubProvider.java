package com.shopping_c_backend.shoppping_c_backend.Component;

import com.alibaba.fastjson.JSON;
import com.shopping_c_backend.shoppping_c_backend.Constants.GitHubConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.GitHubUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ProviderToken;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

@Component
public class GitHubProvider {
    /**
     * 根据用户授权信息获取当前用户的 accessToken
     * 对用户进行授权，先创建一个GET请求，请求github的授权接口中对应用户的访问令牌
     *
     * @param providerToken 用户授权信息
     * @return 当前用户的 accessToken
     */
    private final GitHubConstants gitHubConstants;
    Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10808));

    @Autowired
    public GitHubProvider(GitHubConstants gitHubConstants) {
        this.gitHubConstants = gitHubConstants;
    }

    public String getGitHubToken(ProviderToken providerToken) {
        //1. 创建http请求，构建请求体和请求url等，并向github发起请求
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
        RequestBody body = RequestBody.create(JSON.toJSONString(providerToken), mediaType);
        String url = gitHubConstants.getAccessToken() + "?grant_type=authorization_code&code=" + providerToken.getCode()
                + "&client_id=" + providerToken.getClientId()
                + "&redirect_uri=" + providerToken.getRedirectUri()
                + "&client_secret=" + providerToken.getClientSecret();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //2. 获取github对应的响应消息，根据消息解析出用户的 access token
        try (Response response = client.newCall(request).execute()) {
            String tokenStr = Objects.requireNonNull(response.body()).string();
            String accessToken = null;
            String[] params = tokenStr.split("&");
            for (String param : params) {
                if (param.startsWith("access_token=")) {
                    accessToken = param.split("=")[1];
                    break;
                }
            }
            return accessToken;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    /**
     * 根据用户的 access token 获取当前github用户的详细信息
     *
     * @param accessToken 用户的访问令牌
     * @return github用户对象
     */
    public GitHubUserEntity getGitHubUser(String accessToken) {
        //1. 构建http的GET请求，向github发起请求用户数据
        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .build();
        Request request = new Request.Builder()
                .url(gitHubConstants.getUserInfo() + "?access_token=" + accessToken)
                .addHeader("Authorization", "token " + accessToken)
                .build();

        //2. 获取github对应的响应消息传回来的响应消息，根据消息解析出用户消息
        try {
            Response response = client.newCall(request).execute();
            //String string = response.body().string();
            String gitHubUserStr = Objects.requireNonNull(response.body()).string();
            return JSON.parseObject(gitHubUserStr, GitHubUserEntity.class);
        } catch (Exception e) {
            //log.error("getGiteeUser error,{}", accessToken, e);
            e.getStackTrace();
        }
        return null;
    }
}

