package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.database.DatabaseConnection;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardController {

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
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"message\":\"Only GET method is allowed\"}"
            );

            return;
        }

        try {

            int total =
                    getCount(
                            "SELECT COUNT(*) FROM incidents"
                    );

            int open =
                    getCount(
                            "SELECT COUNT(*) FROM incidents WHERE status = 'OPEN'"
                    );

            int inProgress =
                    getCount(
                            "SELECT COUNT(*) FROM incidents WHERE status = 'IN_PROGRESS'"
                    );

            int resolved =
                    getCount(
                            "SELECT COUNT(*) FROM incidents WHERE status = 'RESOLVED'"
                    );

            int critical =
                    getCount(
                            "SELECT COUNT(*) FROM incidents WHERE severity = 'CRITICAL'"
                    );


            String response =
                    "{"
                    + "\"total\":" + total + ","
                    + "\"open\":" + open + ","
                    + "\"inProgress\":" + inProgress + ","
                    + "\"resolved\":" + resolved + ","
                    + "\"critical\":" + critical
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
                    "{\"message\":\"Unable to load dashboard statistics\"}"
            );
        }
    }


    private int getCount(String sql)
            throws Exception {

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            if (resultSet.next()) {

                return resultSet.getInt(1);
            }
        }

        return 0;
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

        outputStream.write(responseBytes);

        outputStream.close();
    }
}