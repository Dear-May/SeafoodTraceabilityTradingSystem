package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GitHubConstants {

    @Value("${github.appID}")
    private String appID;

    @Value("${github.appKEY}")
    private String appKEY;

    @Value("${github.redirectURI}")
    private String redirectURI;

    @Value("${github.authorizeURL}")
    private String authorizeURL;

    @Value("${github.accessToken}")
    private String accessToken;

    @Value("${github.userInfo}")
    private String userInfo;

}
