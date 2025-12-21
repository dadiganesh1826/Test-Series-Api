package com.testseries.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AdminLoginResponse {
    private boolean success;
    private String token;
    private Map<String, String> admin;
    private String message;
}
