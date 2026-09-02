package com.cybershield.incidentmanagement.controller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class TestController {

    public void handle(HttpExchange exchange) throws IOException {

        String response = """
                {
                    "success": true,
                    "message": "CyberShield backend is running"
                }
                """;

        exchange.getResponseHeaders()
                .set("Content-Type", "application/json");

        exchange.sendResponseHeaders(
                200,
                response.getBytes().length
        );

        OutputStream outputStream =
                exchange.getResponseBody();

        outputStream.write(response.getBytes());

        outputStream.close();
    }
}