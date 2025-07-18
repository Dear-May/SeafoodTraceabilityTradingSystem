package com.shopping_c_backend.shoppping_c_backend.Constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WeatherConstants {
    @Value("${weather.appID}")
    private String appID;

    @Value("${weather.appKEY}")
    private String appKEY;

}
