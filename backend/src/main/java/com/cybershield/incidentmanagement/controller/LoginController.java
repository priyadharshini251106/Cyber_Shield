package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.database.DatabaseConnection;
import com.cybershield.incidentmanagement.service.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    public void handle(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);


        // ==========================================
        // OPTIONS
        // ==========================================

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            exchange.close();

            return;
        }


        // ==========================================
        // POST CHECK
        // ==========================================

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"message\":\"Only POST method is allowed\"}"
            );

            return;
        }


        // ==========================================
        // READ REQUEST BODY
        // ==========================================

        InputStream inputStream =
                exchange.getRequestBody();


        String body =
                new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );


        String email =
                getValue(
                        body,
                        "email"
                );


        String password =
                getValue(
                        body,
                        "password"
                );


        // ==========================================
        // CHECK LOGIN
        // ==========================================

        String[] loginData =
                checkLogin(
                        email,
                        password
                );


        if (loginData != null) {

            String sessionEmail =
                    loginData[0];


            String role =
                    loginData[1];


            // ======================================
            // CREATE SESSION
            // ======================================

            String sessionId =
                    SessionManager.createSession(
                            sessionEmail,
                            role
                    );


            // ======================================
            // RESPONSE
            // ======================================

            String response =
                    "{"
                    + "\"success\":true,"
                    + "\"message\":\"Login Successful\","
                    + "\"sessionId\":\""
                    + sessionId
                    + "\","
                    + "\"role\":\""
                    + role
                    + "\""
                    + "}";


            System.out.println(
                    "Login successful: "
                    + email
                    + " | Role: "
                    + role
            );


            sendResponse(
                    exchange,
                    200,
                    response
            );


        } else {

            String response =
                    "{"
                    + "\"success\":false,"
                    + "\"message\":\"Invalid email or password\""
                    + "}";


            System.out.println(
                    "Login failed: "
                    + email
            );


            sendResponse(
                    exchange,
                    401,
                    response
            );

        }

    }


    // ==========================================
    // CHECK LOGIN
    // ==========================================

    private String[] checkLogin(
            String email,
            String password) {

        String sql = """
                SELECT email, role
                FROM users
                WHERE email = ?
                AND password = ?
                AND status = TRUE
                """;


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email
            );


            statement.setString(
                    2,
                    password
            );


            ResultSet resultSet =
                    statement.executeQuery();


            if (resultSet.next()) {

                String loggedInEmail =
                        resultSet.getString(
                                "email"
                        );


                String role =
                        resultSet.getString(
                                "role"
                        );


                return new String[] {
                        loggedInEmail,
                        role
                };

            }


        } catch (Exception e) {

            e.printStackTrace();

        }


        return null;
    }


    // ==========================================
    // GET JSON VALUE
    // ==========================================

    private String getValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\":\"";


        int start =
                json.indexOf(search);


        if (start == -1) {

            return "";

        }


        start +=
                search.length();


        int end =
                json.indexOf(
                        "\"",
                        start
                );


        if (end == -1) {

            return "";

        }


        return json.substring(
                start,
                end
        );

    }


    // ==========================================
    // CORS
    // ==========================================

    private void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );


        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "POST, OPTIONS"
        );


        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type, Session-Id"
        );

    }


    // ==========================================
    // SEND RESPONSE
    // ==========================================

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );


        byte[] responseBytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );


        OutputStream outputStream =
                exchange.getResponseBody();


        outputStream.write(
                responseBytes
        );


        outputStream.close();

    }

}