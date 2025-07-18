package com.shopping_c_backend.shoppping_c_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
@MapperScan(basePackages = "com.shopping_c_backend.shoppping_c_backend.Mapper")
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
