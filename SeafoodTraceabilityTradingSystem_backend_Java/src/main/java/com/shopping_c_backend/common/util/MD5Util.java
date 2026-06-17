package com.shopping_c_backend.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5Util {
    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String passwordMD5(String userName, String userPassword) {
        String rawPassword = userName + userPassword;
        return passwordEncoder.encode(rawPassword);
    }

    public boolean verifyPassword(String userName, String rawPassword, String encodedPassword) {
        String combined = userName + rawPassword;
        return passwordEncoder.matches(combined, encodedPassword);
    }

    public static PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
