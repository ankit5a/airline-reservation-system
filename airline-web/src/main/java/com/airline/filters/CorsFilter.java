package com.airline.filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;

public class CorsFilter implements Filter {

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        Result result = filterChain.next(context);
        return addCorsHeaders(result);
    }

    public static Result addCorsHeaders(Result result) {
        return result
                .addHeader("Access-Control-Allow-Origin", "*")
                .addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Auth-Token");
    }
}