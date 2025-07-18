package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GoogleConstants {
    @Value("${google.appID}")
    private String appID;

    @Value("${google.appKEY}")
    private String appKEY;

    @Value("${google.redirectURI}")
    private String redirectURI;

    @Value("${google.authorizeURL}")
    private String authorizeURL;

    @Value("${google.accessToken}")
    private String accessToken;

    @Value("${google.userInfo}")
    private String userInfo;

}
