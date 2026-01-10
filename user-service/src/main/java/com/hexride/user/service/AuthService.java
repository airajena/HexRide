package com.hexride.user.service;

import com.hexride.user.dto.request.LoginRequest;
import com.hexride.user.dto.request.RegisterRequest;
import com.hexride.user.dto.response.AuthResponse;

public interface AuthService {
    
    AuthResponse register(RegisterRequest request);
    
    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(String refreshToken);
}
