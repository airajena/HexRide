package com.hexride.user.mapper;

import com.hexride.user.dto.response.UserResponse;
import com.hexride.user.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-08T09:25:43+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.phoneNumber( user.getPhoneNumber() );
        userResponse.email( user.getEmail() );
        userResponse.firstName( user.getFirstName() );
        userResponse.lastName( user.getLastName() );
        userResponse.userType( user.getUserType() );
        userResponse.createdAt( user.getCreatedAt() );

        return userResponse.build();
    }
}
