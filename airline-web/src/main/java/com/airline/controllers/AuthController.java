package com.airline.controllers;

import com.airline.web.dtos.LoginDto;
import com.airline.web.dtos.UserDto;
import ninja.Context;
import ninja.Result;

public interface AuthController {
    Result register(Context context, UserDto userDto);
    Result login(Context context, LoginDto loginDto);
    Result logout(Context context);
}