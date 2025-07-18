package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BaiduOCRConstants {
    @Value("${baidu.AppID}")
    private String AppID;

    @Value("${baidu.APIKey}")
    private String APIKey;
}
