package com.shopping_c_backend.shoppping_c_backend.Component;

import com.alibaba.fastjson.JSON;
import com.shopping_c_backend.shoppping_c_backend.Constants.GiteeConstants;
import com.shopping_c_backend.shoppping_c_backend.Entity.GiteeUserEntity;
import com.shopping_c_backend.shoppping_c_backend.Entity.ProviderToken;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GiteeProvider {
    /**
     * 根据用户授权信息获取当前用户的 accessToken
     * 对用户进行授权，先创建一个GET请求，请求gitee中对应用户的访问令牌
     *
     * @param providerToken 用户授权信息
     * @return 当前用户的 accessToken
     */
    private final GiteeConstants giteeConstants;

    @Autowired
    public GiteeProvider(GiteeConstants giteeConstants) {
        this.giteeConstants = giteeConstants;
    }

    public String getGiteeToken(ProviderToken providerToken) {
        //1. 创建http请求，构建请求体和请求url等，并向gitee发起请求
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON.toJSONString(providerToken), mediaType);
        String url = giteeConstants.getAccessToken() + "?grant_type=authorization_code&code=" + providerToken.getCode()
                + "&client_id=" + providerToken.getClientId()
                + "&redirect_uri=" + providerToken.getRedirectUri()
                + "&client_secret=" + providerToken.getClientSecret();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        //2. 获取gitee对应的响应消息，根据消息解析出用户的 access token
        try (Response response = client.newCall(request).execute()) {
            String tokenStr = Objects.requireNonNull(response.body()).string();
            String accessToken = tokenStr.split(",")[0].split(":")[1];
            accessToken = accessToken.substring(1, accessToken.length() - 1);
            //System.out.println("accessToken = " + accessToken);
            return accessToken;
        } catch (Exception e) {
            e.getStackTrace();
            //log.error("getAccessToken error,{}", accessTokenDTO, e);
        }
        return null;
    }

    /**
     * 根据用户的 access token 获取当前gitee用户的详细信息
     *
     * @param accessToken 用户的访问令牌
     * @return gitee用户对象
     */
    public GiteeUserEntity getGiteeUser(String accessToken) {
        //1. 构建http的GET请求，向gitee请求用户数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(giteeConstants.getUserInfo() + "?access_token=" + accessToken).build();

        //2. 获取gitee传回来的响应消息，根据消息解析出用户消息
        try {
            Response response = client.newCall(request).execute();
            String giteeUserStr = Objects.requireNonNull(response.body()).string();
            return JSON.parseObject(giteeUserStr, GiteeUserEntity.class);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }
}

