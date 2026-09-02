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

public class UserManagementController {

    private static final String ADMIN_EMAIL =
            "admin@cybershield.com";


    // ==========================================
    // MAIN HANDLER
    // ==========================================

    public void handle(
            HttpExchange exchange)
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
        // SESSION CHECK
        // ==========================================

        String sessionId =
                exchange.getRequestHeaders()
                        .getFirst("Session-Id");


        if (!SessionManager.isValid(
                sessionId
        )) {

            sendResponse(
                    exchange,
                    401,
                    "{\"message\":\"Unauthorized. Please login.\"}"
            );

            return;
        }


        // ==========================================
        // ADMIN CHECK
        // ==========================================

        if (!SessionManager.isAdmin(
                sessionId
        )) {

            sendResponse(
                    exchange,
                    403,
                    "{\"message\":\"Access denied. Admin only.\"}"
            );

            return;
        }


        String method =
                exchange.getRequestMethod();


        String path =
                exchange.getRequestURI()
                        .getPath();


        // ==========================================
        // GET ALL USERS
        // ==========================================

        if (
                path.equals("/api/users")
                &&
                method.equalsIgnoreCase("GET")
        ) {

            getAllUsers(exchange);

            return;
        }


        // ==========================================
        // USER ID
        // ==========================================

        if (
                path.startsWith(
                        "/api/users/"
                )
        ) {

            String idText =
                    path.substring(
                            "/api/users/"
                                    .length()
                    );


            int userId;

            try {

                userId =
                        Integer.parseInt(
                                idText
                        );

            } catch (Exception e) {

                sendResponse(
                        exchange,
                        400,
                        "{\"message\":\"Invalid user ID\"}"
                );

                return;
            }


            // ======================================
            // CHANGE ROLE
            // ======================================

            if (
                    path.endsWith("/role")
                    &&
                    method.equalsIgnoreCase("PUT")
            ) {

                String basePath =
                        path.substring(
                                0,
                                path.length() -
                                "/role".length()
                        );


                String baseIdText =
                        basePath.substring(
                                "/api/users/"
                                        .length()
                        );


                try {

                    userId =
                            Integer.parseInt(
                                    baseIdText
                            );

                } catch (Exception e) {

                    sendResponse(
                            exchange,
                            400,
                            "{\"message\":\"Invalid user ID\"}"
                    );

                    return;
                }


                updateRole(
                        exchange,
                        userId
                );

                return;
            }


            // ======================================
            // CHANGE STATUS
            // ======================================

            if (
                    path.endsWith("/status")
                    &&
                    method.equalsIgnoreCase("PUT")
            ) {

                String basePath =
                        path.substring(
                                0,
                                path.length() -
                                "/status".length()
                        );


                String baseIdText =
                        basePath.substring(
                                "/api/users/"
                                        .length()
                        );


                try {

                    userId =
                            Integer.parseInt(
                                    baseIdText
                            );

                } catch (Exception e) {

                    sendResponse(
                            exchange,
                            400,
                            "{\"message\":\"Invalid user ID\"}"
                    );

                    return;
                }


                updateStatus(
                        exchange,
                        userId
                );

                return;
            }
        }


        // ==========================================
        // NOT FOUND
        // ==========================================

        sendResponse(
                exchange,
                404,
                "{\"message\":\"Endpoint not found\"}"
        );

    }


    // ==========================================
    // GET ALL USERS
    // ==========================================

    private void getAllUsers(
            HttpExchange exchange)
            throws IOException {

        String sql = """
                SELECT id, name, email, role, status
                FROM users
                ORDER BY id
                """;


        StringBuilder json =
                new StringBuilder();


        json.append("[");


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            boolean first = true;


            while (resultSet.next()) {

                if (!first) {

                    json.append(",");

                }


                first = false;


                json.append("{");


                json.append(
                        "\"id\":"
                        + resultSet.getInt("id")
                );


                json.append(",");


                json.append(
                        "\"name\":\""
                        + escapeJson(
                                resultSet.getString(
                                        "name"
                                )
                        )
                        + "\""
                );


                json.append(",");


                json.append(
                        "\"email\":\""
                        + escapeJson(
                                resultSet.getString(
                                        "email"
                                )
                        )
                        + "\""
                );


                json.append(",");


                json.append(
                        "\"role\":\""
                        + escapeJson(
                                resultSet.getString(
                                        "role"
                                )
                        )
                        + "\""
                );


                json.append(",");


                json.append(
                        "\"status\":"
                        + resultSet.getBoolean(
                                "status"
                        )
                );


                json.append("}");

            }


        } catch (Exception e) {

            e.printStackTrace();


            sendResponse(
                    exchange,
                    500,
                    "{\"message\":\"Unable to load users\"}"
            );

            return;
        }


        json.append("]");


        sendResponse(
                exchange,
                200,
                json.toString()
        );

    }


    // ==========================================
    // UPDATE ROLE
    // ==========================================

    private void updateRole(
            HttpExchange exchange,
            int userId)
            throws IOException {

        String body =
                readRequestBody(exchange);


        String role =
                getValue(
                        body,
                        "role"
                );


        if (
                role == null
                ||
                (
                    !role.equalsIgnoreCase("USER")
                    &&
                    !role.equalsIgnoreCase("ADMIN")
                )
        ) {

            sendResponse(
                    exchange,
                    400,
                    "{\"message\":\"Invalid role. Use USER or ADMIN.\"}"
            );

            return;
        }


        // ==========================================
        // PROTECT DEFAULT ADMIN
        // ==========================================

        if (isDefaultAdmin(userId)) {

            sendResponse(
                    exchange,
                    403,
                    "{\"message\":\"Default admin account cannot be changed.\"}"
            );

            return;
        }


        String sql = """
                UPDATE users
                SET role = ?
                WHERE id = ?
                """;


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    role.toUpperCase()
            );


            statement.setInt(
                    2,
                    userId
            );


            int rows =
                    statement.executeUpdate();


            if (rows > 0) {

                sendResponse(
                        exchange,
                        200,
                        "{\"success\":true,\"message\":\"User role updated successfully\"}"
                );

            } else {

                sendResponse(
                        exchange,
                        404,
                        "{\"success\":false,\"message\":\"User not found\"}"
                );

            }


        } catch (Exception e) {

            e.printStackTrace();


            sendResponse(
                    exchange,
                    500,
                    "{\"message\":\"Unable to update user role\"}"
            );

        }

    }


    // ==========================================
    // UPDATE STATUS
    // ==========================================

    private void updateStatus(
            HttpExchange exchange,
            int userId)
            throws IOException {

        String body =
                readRequestBody(exchange);


        String statusText =
                getValue(
                        body,
                        "status"
                );


        boolean status;


        if (
                statusText.equalsIgnoreCase(
                        "true"
                )
        ) {

            status = true;

        } else if (
                statusText.equalsIgnoreCase(
                        "false"
                )
        ) {

            status = false;

        } else {

            sendResponse(
                    exchange,
                    400,
                    "{\"message\":\"Invalid status. Use true or false.\"}"
            );

            return;
        }


        // ==========================================
        // PROTECT DEFAULT ADMIN
        // ==========================================

        if (isDefaultAdmin(userId)) {

            sendResponse(
                    exchange,
                    403,
                    "{\"message\":\"Default admin account cannot be changed.\"}"
            );

            return;
        }


        String sql = """
                UPDATE users
                SET status = ?
                WHERE id = ?
                """;


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setBoolean(
                    1,
                    status
            );


            statement.setInt(
                    2,
                    userId
            );


            int rows =
                    statement.executeUpdate();


            if (rows > 0) {

                sendResponse(
                        exchange,
                        200,
                        "{\"success\":true,\"message\":\"User status updated successfully\"}"
                );

            } else {

                sendResponse(
                        exchange,
                        404,
                        "{\"success\":false,\"message\":\"User not found\"}"
                );

            }


        } catch (Exception e) {

            e.printStackTrace();


            sendResponse(
                    exchange,
                    500,
                    "{\"message\":\"Unable to update user status\"}"
            );

        }

    }


    // ==========================================
    // CHECK DEFAULT ADMIN
    // ==========================================

    private boolean isDefaultAdmin(
            int userId) {

        String sql = """
                SELECT email
                FROM users
                WHERE id = ?
                """;


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    userId
            );


            ResultSet resultSet =
                    statement.executeQuery();


            if (resultSet.next()) {

                String email =
                        resultSet.getString(
                                "email"
                        );


                return ADMIN_EMAIL.equalsIgnoreCase(
                        email
                );

            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        return false;

    }


    // ==========================================
    // READ BODY
    // ==========================================

    private String readRequestBody(
            HttpExchange exchange)
            throws IOException {

        InputStream inputStream =
                exchange.getRequestBody();


        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );

    }


    // ==========================================
    // GET JSON VALUE
    // ==========================================

    private String getValue(
            String json,
            String key) {

        String search =
                "\"" + key + "\"";


        int keyPosition =
                json.indexOf(search);


        if (keyPosition == -1) {

            return "";

        }


        int colonPosition =
                json.indexOf(
                        ":",
                        keyPosition
                );


        if (colonPosition == -1) {

            return "";

        }


        int start =
                colonPosition + 1;


        while (
                start < json.length()
                &&
                Character.isWhitespace(
                        json.charAt(start)
                )
        ) {

            start++;

        }


        if (
                start < json.length()
                &&
                json.charAt(start) == '"'
        ) {

            start++;


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


        int end = start;


        while (
                end < json.length()
                &&
                json.charAt(end) != ','
                &&
                json.charAt(end) != '}'
        ) {

            end++;

        }


        return json.substring(
                start,
                end
        ).trim();

    }


    // ==========================================
    // ESCAPE JSON
    // ==========================================

    private String escapeJson(
            String value) {

        if (value == null) {

            return "";

        }


        return value
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
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
                "GET, PUT, OPTIONS"
        );


        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type,Session-Id"
        );

    }


    // ==========================================
    // RESPONSE
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