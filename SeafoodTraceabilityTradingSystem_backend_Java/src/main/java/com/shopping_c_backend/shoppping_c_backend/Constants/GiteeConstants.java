package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GiteeConstants {
    @Value("${gitee.appID}")
    private String appID;

    @Value("${gitee.appKEY}")
    private String appKEY;

    @Value("${gitee.redirectURI}")
    private String redirectURI;

    @Value("${gitee.authorizeURL}")
    private String authorizeURL;

    @Value("${gitee.accessToken}")
    private String accessToken;

    @Value("${gitee.userInfo}")
    private String userInfo;

}
