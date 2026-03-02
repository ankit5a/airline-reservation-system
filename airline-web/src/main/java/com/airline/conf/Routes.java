package com.airline.conf;

import com.airline.controllers.*;
import com.airline.enums.UserAction;
import ninja.Router;
import ninja.application.ApplicationRoutes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Routes implements ApplicationRoutes {

    // Stores method name → required actions for ACL filter
    public static final Map<String, List<UserAction>> userActionMethodInvokeMap = new HashMap<>();

    private static String acl(String methodName, UserAction... actions) {
        userActionMethodInvokeMap.put(methodName, Arrays.asList(actions));
        return methodName;
    }

    @Override
    public void init(Router router) {

        // ============================
        // AUTH ROUTES (Public)
        // ============================
        router.POST().route("/api/auth/register").with(AuthController.class, "register");
        router.POST().route("/api/auth/login").with(AuthController.class, "login");
        router.POST().route("/api/auth/logout").with(AuthController.class, "logout");

        // ============================
        // FLIGHT ROUTES
        // ============================
        // Public
        router.GET().route("/api/flights").with(FlightController.class, "getAllFlights");
        router.GET().route("/api/flights/{id}").with(FlightController.class, "getFlightById");
        router.GET().route("/api/flights/search").with(FlightController.class, "searchFlights");

        // Admin only
        router.POST().route("/api/flights").with(FlightController.class,
                acl("createFlight", UserAction.MANAGE_FLIGHTS));
        router.PUT().route("/api/flights/{id}").with(FlightController.class,
                acl("updateFlight", UserAction.MANAGE_FLIGHTS));
        router.DELETE().route("/api/flights/{id}").with(FlightController.class,
                acl("deleteFlight", UserAction.MANAGE_FLIGHTS));

        // ============================
        // BOOKING ROUTES
        // ============================
        router.POST().route("/api/bookings").with(BookingController.class,
                acl("createBooking", UserAction.CREATE_BOOKING));
        router.GET().route("/api/bookings/{id}").with(BookingController.class,
                acl("getBookingById", UserAction.VIEW_OWN_BOOKINGS));
        router.GET().route("/api/bookings").with(BookingController.class,
                acl("getAllBookings", UserAction.VIEW_ALL_BOOKINGS));
        router.GET().route("/api/bookings/my").with(BookingController.class,
                acl("getMyBookings", UserAction.VIEW_OWN_BOOKINGS));
        router.DELETE().route("/api/bookings/{id}").with(BookingController.class,
                acl("cancelBooking", UserAction.CANCEL_BOOKING));

        // ============================
        // CORS OPTIONS ROUTES
        // ============================
        router.OPTIONS().route("/api/flights").with(CorsController.class, "handleOptions");
        router.OPTIONS().route("/api/flights/{id}").with(CorsController.class, "handleOptions");
        router.OPTIONS().route("/api/bookings").with(CorsController.class, "handleOptions");
        router.OPTIONS().route("/api/bookings/{id}").with(CorsController.class, "handleOptions");
        router.OPTIONS().route("/api/auth/register").with(CorsController.class, "handleOptions");
        router.OPTIONS().route("/api/auth/login").with(CorsController.class, "handleOptions");
    }
}