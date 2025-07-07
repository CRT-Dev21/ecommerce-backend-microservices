package com.example.gateway_service.application.mapper;

import com.example.gateway_service.application.dto.request.UserDto;
import com.example.gateway_service.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserMapper {

    public static User mapDtoToUser(UserDto userDto) {
        return new User(userDto.getName(), userDto.getEmail(), userDto.getPassword(), userDto.getRole());
    }

}
