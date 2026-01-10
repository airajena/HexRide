package com.hexride.user.dto.response;

import com.hexride.common.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private UserType userType;
    private LocalDateTime createdAt;
}
