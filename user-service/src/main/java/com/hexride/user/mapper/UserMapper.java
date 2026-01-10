package com.hexride.user.mapper;

import com.hexride.user.dto.response.UserResponse;
import com.hexride.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    UserResponse toResponse(User user);
}
