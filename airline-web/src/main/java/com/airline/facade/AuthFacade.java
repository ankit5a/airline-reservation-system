package com.airline.facade;

import com.airline.web.dtos.UserDto;

public interface AuthFacade {
    UserDto register(UserDto userDto);
    UserDto login(String username, String password);
}