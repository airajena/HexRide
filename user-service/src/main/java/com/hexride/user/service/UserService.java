package com.hexride.user.service;

import com.hexride.user.dto.response.UserResponse;

public interface UserService {
    
    UserResponse getUserById(String userId);
    
    UserResponse updateUser(String userId, String firstName, String lastName, String email);
}
