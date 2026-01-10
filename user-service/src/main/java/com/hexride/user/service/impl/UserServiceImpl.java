package com.hexride.user.service.impl;

import com.hexride.common.exception.UserNotFoundException;
import com.hexride.user.dto.response.UserResponse;
import com.hexride.user.entity.User;
import com.hexride.user.mapper.UserMapper;
import com.hexride.user.repository.UserRepository;
import com.hexride.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String userId, String firstName, String lastName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) user.setEmail(email);

        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
