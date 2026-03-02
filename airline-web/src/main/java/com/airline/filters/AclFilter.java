package com.airline.filters;

import com.airline.conf.Routes;
import com.airline.enums.UserAction;
import com.airline.enums.UserRole;
import com.airline.web.dtos.ApiResponseDto;
import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;

import java.util.*;

public class AclFilter implements Filter {

    // Role → allowed actions mapping
    private static final Map<UserRole, List<UserAction>> ROLE_ACTIONS = new HashMap<>();

    static {
        ROLE_ACTIONS.put(UserRole.ADMIN, Arrays.asList(
                UserAction.MANAGE_FLIGHTS,
                UserAction.VIEW_FLIGHTS,
                UserAction.CREATE_BOOKING,
                UserAction.CANCEL_BOOKING,
                UserAction.VIEW_ALL_BOOKINGS,
                UserAction.VIEW_OWN_BOOKINGS,
                UserAction.MANAGE_USERS,
                UserAction.VIEW_OWN_PROFILE
        ));

        ROLE_ACTIONS.put(UserRole.CUSTOMER, Arrays.asList(
                UserAction.VIEW_FLIGHTS,
                UserAction.CREATE_BOOKING,
                UserAction.CANCEL_BOOKING,
                UserAction.VIEW_OWN_BOOKINGS,
                UserAction.VIEW_OWN_PROFILE
        ));
    }

    @Override
    public Result filter(FilterChain filterChain, Context context) {
        Session session = context.getSession();
        String token = context.getHeader("X-Auth-Token");

        if (token == null || token.isEmpty()) {
            token = session.get("authToken");
        }

        if (token == null || token.isEmpty()) {
            return Results.unauthorized().json()
                    .render(ApiResponseDto.error("Authentication required"));
        }

        String roleStr = session.get("userRole");
        if (roleStr == null) {
            return Results.unauthorized().json()
                    .render(ApiResponseDto.error("Session expired. Please login again."));
        }

        UserRole userRole;
        try {
            userRole = UserRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            return Results.forbidden().json()
                    .render(ApiResponseDto.error("Invalid user role"));
        }

        // Get the method name from the context to look up required actions
        String methodName = context.getRoute().getControllerMethod().getName();
        List<UserAction> requiredActions = Routes.userActionMethodInvokeMap.get(methodName);

        if (requiredActions != null && !requiredActions.isEmpty()) {
            List<UserAction> userActions = ROLE_ACTIONS.getOrDefault(userRole, Collections.emptyList());
            for (UserAction required : requiredActions) {
                if (!userActions.contains(required)) {
                    return Results.forbidden().json()
                            .render(ApiResponseDto.error("Insufficient permissions: " + required));
                }
            }
        }

        return filterChain.next(context);
    }
}