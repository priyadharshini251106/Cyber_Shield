package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.service.SessionManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LogoutController {

    public void handle(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);
            exchange.close();

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"message\":\"Only POST method is allowed\"}"
            );

            return;
        }


        String sessionId =
                exchange.getRequestHeaders()
                        .getFirst("Session-Id");


        if (sessionId != null) {

            SessionManager
                    .removeSession(sessionId);
        }


        sendResponse(
                exchange,
                200,
                "{\"message\":\"Logout Successful\"}"
        );


        System.out.println(
                "User logged out."
        );
    }


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

        outputStream.write(responseBytes);

        outputStream.close();
    }
}