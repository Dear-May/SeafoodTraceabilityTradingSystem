package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WechatConstants {
    @Value("${wechat.appID}")
    private String appID;

    @Value("${wechat.appKEY}")
    private String appKEY;

    @Value("${wechat.accessToken}")
    private String accessToken;

    @Value("${wechat.userInfo}")
    private String userInfo;

}
