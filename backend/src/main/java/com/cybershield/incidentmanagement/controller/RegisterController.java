package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.entity.User;
import com.cybershield.incidentmanagement.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegisterController {

    private final UserService service = new UserService();

    public void handle(HttpExchange exchange) throws IOException {

        // CORS headers
        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );

        // Handle browser preflight request
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }

        // Only POST is allowed
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            String response =
                    "{\"message\":\"Only POST method is allowed\"}";

            sendResponse(exchange, 405, response);

            return;
        }

        // Read request body
        InputStream inputStream =
                exchange.getRequestBody();

        String body =
                new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );

        System.out.println("Received request:");
        System.out.println(body);

        // Extract values
        String name =
                getValue(body, "name");

        String email =
                getValue(body, "email");

        String password =
                getValue(body, "password");

        System.out.println("Name: " + name);
        System.out.println("Email: " + email);

        // Create User
        User user =
                new User(
                        name,
                        email,
                        password
                );

        // Register user
        boolean success =
                service.register(user);

        String response;

        if (success) {

            response =
                    "{\"message\":\"Registration Successful\"}";

            System.out.println(
                    "User registered successfully."
            );

        } else {

            response =
                    "{\"message\":\"Registration Failed\"}";

            System.out.println(
                    "User registration failed."
            );
        }

        sendResponse(exchange, 200, response);
    }


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

        start += search.length();

        int end =
                json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(start, end);
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
                response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        OutputStream outputStream =
                exchange.getResponseBody();

        outputStream.write(responseBytes);

        outputStream.close();
    }
}