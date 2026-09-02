package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.database.DatabaseConnection;
import com.cybershield.incidentmanagement.service.SessionManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserProfileController {

    public void handle(
            HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(
                    204,
                    -1
            );

            exchange.close();

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"message\":\"Only GET method is allowed\"}"
            );

            return;
        }


        String sessionId =
                exchange.getRequestHeaders()
                        .getFirst("Session-Id");


        if (!SessionManager.isValid(sessionId)) {

            sendResponse(
                    exchange,
                    401,
                    "{\"message\":\"Invalid or expired session\"}"
            );

            return;
        }


        String email =
                SessionManager.getEmail(sessionId);


        String sql = """
                SELECT
                    id,
                    name,
                    email,
                    role,
                    status
                FROM users
                WHERE email = ?
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


            ResultSet resultSet =
                    statement.executeQuery();


            if (!resultSet.next()) {

                sendResponse(
                        exchange,
                        404,
                        "{\"message\":\"User not found\"}"
                );

                return;
            }


            int id =
                    resultSet.getInt("id");

            String name =
                    resultSet.getString("name");

            String userEmail =
                    resultSet.getString("email");

            String role =
                    resultSet.getString("role");

            boolean status =
                    resultSet.getBoolean("status");


            String response =
                    "{"
                    + "\"id\":" + id + ","
                    + "\"name\":\""
                    + escapeJson(name)
                    + "\","
                    + "\"email\":\""
                    + escapeJson(userEmail)
                    + "\","
                    + "\"role\":\""
                    + escapeJson(role)
                    + "\","
                    + "\"status\":"
                    + status
                    + "}";


            sendResponse(
                    exchange,
                    200,
                    response
            );


        } catch (Exception e) {

            e.printStackTrace();


            sendResponse(
                    exchange,
                    500,
                    "{\"message\":\"Unable to load profile\"}"
            );
        }
    }


    private String escapeJson(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }


    private void addCorsHeaders(
            HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type, Session-Id"
        );
    }


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