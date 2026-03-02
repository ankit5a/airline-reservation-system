package com.airline.controllers;

import com.airline.filters.CorsFilter;
import ninja.Result;
import ninja.Results;

import javax.inject.Singleton;

@Singleton
public class CorsControllerImpl implements CorsController {

    @Override
    public Result handleOptions() {
        return CorsFilter.addCorsHeaders(Results.ok());
    }
}