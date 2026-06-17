package com.shopping_c_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication(scanBasePackages = "com.shopping_c_backend")
@Controller
@MapperScan(basePackages = {
    "com.shopping_c_backend.module.user",
    "com.shopping_c_backend.module.goods",
    "com.shopping_c_backend.module.order",
    "com.shopping_c_backend.module.shop",
    "com.shopping_c_backend.module.address",
    "com.shopping_c_backend.module.cart",
    "com.shopping_c_backend.module.favorite",
    "com.shopping_c_backend.module.footprint",
    "com.shopping_c_backend.module.admin",
    "com.shopping_c_backend.module.live",
    "com.shopping_c_backend.module.chat",
    "com.shopping_c_backend.module.trace"
})
public class ShoppingCBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCBackendApplication.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/pay/paygoods")
    public String demoPayment(String orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "pay-goods";
    }
}
