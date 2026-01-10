package com.hexride.user.controller;

import com.hexride.common.dto.ApiResponse;
import com.hexride.user.dto.request.UpdateUserRequest;
import com.hexride.user.dto.response.UserResponse;
import com.hexride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal String userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable String userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @AuthenticationPrincipal String userId,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail()
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
