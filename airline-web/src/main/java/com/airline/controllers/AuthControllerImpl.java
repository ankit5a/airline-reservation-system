package com.airline.controllers;

import com.airline.exceptions.InvalidRequestException;
import com.airline.facade.AuthFacade;
import com.airline.filters.CorsFilter;
import com.airline.web.dtos.ApiResponseDto;
import com.airline.web.dtos.LoginDto;
import com.airline.web.dtos.UserDto;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthControllerImpl implements AuthController {

    @Inject
    private AuthFacade authFacade;

    @Override
    public Result register(Context context, UserDto userDto) {
        try {
            UserDto created = authFacade.register(userDto);
            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("User registered successfully", created))
            );
        } catch (InvalidRequestException e) {
            return CorsFilter.addCorsHeaders(
                    Results.badRequest().json().render(ApiResponseDto.error(e.getMessage()))
            );
        } catch (Exception e) {
            return CorsFilter.addCorsHeaders(
                    Results.internalServerError().json().render(ApiResponseDto.error("Registration failed: " + e.getMessage()))
            );
        }
    }

    @Override
    public Result login(Context context, LoginDto loginDto) {
        try {
            UserDto userDto = authFacade.login(loginDto.getUsername(), loginDto.getPassword());
            // Store in session
            Session session = context.getSession();
            session.put("userId", String.valueOf(userDto.getId()));
            session.put("username", userDto.getUsername());
            session.put("userRole", userDto.getRole());
            session.put("authToken", userDto.getToken());

            return CorsFilter.addCorsHeaders(
                    Results.ok().json().render(ApiResponseDto.ok("Login successful", userDto))
            );
        } catch (InvalidRequestException e) {
            return CorsFilter.addCorsHeaders(
                    Results.unauthorized().json().render(ApiResponseDto.error(e.getMessage()))
            );
        }
    }

    @Override
    public Result logout(Context context) {
        context.getSession().clear();
        return CorsFilter.addCorsHeaders(
                Results.ok().json().render(ApiResponseDto.ok("Logged out successfully", null))
        );
    }
}