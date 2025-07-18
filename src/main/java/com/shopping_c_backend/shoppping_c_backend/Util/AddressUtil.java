package com.shopping_c_backend.shoppping_c_backend.Util;

import java.util.ArrayList;
import java.util.List;

public class AddressUtil {
    public static List<String> extractAddress(String address) {
        List<String> components = new ArrayList<>();

        if (address.startsWith("北京市") || address.startsWith("天津市") ||
                address.startsWith("上海市") || address.startsWith("重庆市")) {
            // 直接添加直辖市和区
            components.add("北京");
            String[] parts = address.substring(3).split("区|县");
            if (parts.length > 0) {
                components.add(parts[0] + "区"); // 添加区名
            }
            if (parts.length > 1) {
                components.add(parts[1]); // 添加街道或详细地址
            }
        } else {
            // 处理一般省市区
            String[] parts = address.split("省|市|区|县");
            components.add(parts[0]); // 省
            if (parts.length > 1) {
                components.add(parts[1] + "市"); // 市
            }
            if (parts.length > 2) {
                components.add(parts[2] + "区"); // 区
            }
            if (parts.length > 3) {
                components.add(address.substring(address.indexOf(parts[2]) + parts[2].length() + 1));
            }
        }

        return components;
    }
}

