package com.cybershield.incidentmanagement.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    // ==========================================
    // SESSION DATA
    // ==========================================

    private static final Map<String, String> sessions =
            new HashMap<>();


    private static final Map<String, String> roles =
            new HashMap<>();


    // ==========================================
    // CREATE SESSION
    // ==========================================

    public static String createSession(
            String email,
            String role) {

        String sessionId =
                UUID.randomUUID().toString();


        sessions.put(
                sessionId,
                email
        );


        roles.put(
                sessionId,
                role
        );


        return sessionId;
    }


    // ==========================================
    // GET EMAIL
    // ==========================================

    public static String getEmail(
            String sessionId) {

        return sessions.get(
                sessionId
        );
    }


    // ==========================================
    // GET ROLE
    // ==========================================

    public static String getRole(
            String sessionId) {

        return roles.get(
                sessionId
        );
    }


    // ==========================================
    // CHECK SESSION
    // ==========================================

    public static boolean isValid(
            String sessionId) {

        return sessionId != null
                && sessions.containsKey(
                    sessionId
                );
    }


    // ==========================================
    // CHECK ADMIN
    // ==========================================

    public static boolean isAdmin(
            String sessionId) {

        String role =
                getRole(sessionId);

        return role != null
                && role.equalsIgnoreCase(
                    "ADMIN"
                );
    }


    // ==========================================
    // REMOVE SESSION
    // ==========================================

    public static void removeSession(
            String sessionId) {

        sessions.remove(
                sessionId
        );

        roles.remove(
                sessionId
        );
    }
}