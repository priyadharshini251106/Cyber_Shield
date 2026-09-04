package com.cybershield.incidentmanagement.server;

import com.cybershield.incidentmanagement.controller.DashboardController;
import com.cybershield.incidentmanagement.controller.IncidentController;
import com.cybershield.incidentmanagement.controller.LoginController;
import com.cybershield.incidentmanagement.controller.LogoutController;
import com.cybershield.incidentmanagement.controller.RegisterController;
import com.cybershield.incidentmanagement.controller.TestController;
import com.cybershield.incidentmanagement.controller.UserManagementController;
import com.cybershield.incidentmanagement.controller.UserProfileController;
import com.cybershield.incidentmanagement.database.DatabaseInitializer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class CyberShieldServer {

    public static void main(String[] args)
            throws IOException {

        // ------------------------------------------
        // Render PORT
        // ------------------------------------------

        String portValue = System.getenv("PORT");

        int port = 8080;

        if (portValue != null && !portValue.isEmpty()) {

            try {

                port = Integer.parseInt(portValue);

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid PORT value. Using port 8080."
                );

                port = 8080;
            }
        }


        // ------------------------------------------
        // Initialize Database
        // ------------------------------------------

        DatabaseInitializer.initialize();


        // ------------------------------------------
        // Create HTTP Server
        // ------------------------------------------

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(
                                "0.0.0.0",
                                port
                        ),
                        0
                );


        // ------------------------------------------
        // Controllers
        // ------------------------------------------

        TestController testController =
                new TestController();

        RegisterController registerController =
                new RegisterController();

        LoginController loginController =
                new LoginController();

        LogoutController logoutController =
                new LogoutController();

        IncidentController incidentController =
                new IncidentController();

        DashboardController dashboardController =
                new DashboardController();

        UserProfileController profileController =
                new UserProfileController();

        UserManagementController userManagementController =
                new UserManagementController();


        // ------------------------------------------
        // API Routes
        // ------------------------------------------

        server.createContext(
                "/api/test",
                testController::handle
        );

        server.createContext(
                "/api/register",
                registerController::handle
        );

        server.createContext(
                "/api/login",
                loginController::handle
        );

        server.createContext(
                "/api/logout",
                logoutController::handle
        );

        server.createContext(
                "/api/incidents",
                incidentController::handle
        );

        server.createContext(
                "/api/dashboard",
                dashboardController::handle
        );

        server.createContext(
                "/api/profile",
                profileController::handle
        );

        server.createContext(
                "/api/users",
                userManagementController::handle
        );


        // ------------------------------------------
        // Frontend
        // ------------------------------------------

        server.createContext(
                "/",
                CyberShieldServer::serveFrontend
        );


        // ------------------------------------------
        // Start Server
        // ------------------------------------------

        server.setExecutor(null);

        server.start();


        // ------------------------------------------
        // Console Information
        // ------------------------------------------

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "       CYBERSHIELD SERVER"
        );

        System.out.println(
                "------------------------------------------"
        );

        System.out.println(
                "Server started successfully"
        );

        System.out.println(
                "Host: 0.0.0.0"
        );

        System.out.println(
                "Port: " + port
        );

        System.out.println(
                "Frontend and API are running"
        );

        System.out.println(
                "------------------------------------------"
        );
    }


    // ==================================================
    // FRONTEND FILE SERVER
    // ==================================================

    private static void serveFrontend(
            HttpExchange exchange) {

        try {

            String requestPath =
                    exchange.getRequestURI().getPath();


            // ------------------------------------------
            // Root URL
            // ------------------------------------------

            if (requestPath.equals("/")) {

                requestPath = "/index.html";
            }


            // ------------------------------------------
            // Prevent path traversal
            // ------------------------------------------

            if (requestPath.contains("..")) {

                sendText(
                        exchange,
                        403,
                        "Forbidden"
                );

                return;
            }


            // ------------------------------------------
            // Frontend directory
            // ------------------------------------------

            Path frontendPath =
                    Path.of(
                            "/app/frontend"
                    );


            String relativePath =
                    requestPath.substring(1);


            Path requestedFile =
                    frontendPath.resolve(relativePath)
                               .normalize();


            // ------------------------------------------
            // Security check
            // ------------------------------------------

            if (!requestedFile.startsWith(
                    frontendPath.normalize())) {

                sendText(
                        exchange,
                        403,
                        "Forbidden"
                );

                return;
            }


            // ------------------------------------------
            // Check file
            // ------------------------------------------

            File file =
                    requestedFile.toFile();


            if (!file.exists()
                    || !file.isFile()) {

                sendText(
                        exchange,
                        404,
                        "404 - File Not Found"
                );

                return;
            }


            // ------------------------------------------
            // Read file
            // ------------------------------------------

            byte[] content =
                    Files.readAllBytes(
                            requestedFile
                    );


            // ------------------------------------------
            // Content Type
            // ------------------------------------------

            String contentType =
                    getContentType(
                            requestedFile
                    );


            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            contentType
                    );


            exchange.getResponseHeaders()
                    .set(
                            "Cache-Control",
                            "no-cache"
                    );


            // ------------------------------------------
            // Send Response
            // ------------------------------------------

            exchange.sendResponseHeaders(
                    200,
                    content.length
            );


            try (OutputStream output =
                         exchange.getResponseBody()) {

                output.write(content);
            }

        } catch (Exception e) {

            e.printStackTrace();

            try {

                sendText(
                        exchange,
                        500,
                        "Internal Server Error"
                );

            } catch (IOException ignored) {
            }
        }
    }


    // ==================================================
    // CONTENT TYPE
    // ==================================================

    private static String getContentType(
            Path file) {

        String fileName =
                file.getFileName()
                    .toString()
                    .toLowerCase();


        if (fileName.endsWith(".html")) {

            return "text/html; charset=UTF-8";
        }

        if (fileName.endsWith(".css")) {

            return "text/css; charset=UTF-8";
        }

        if (fileName.endsWith(".js")) {

            return "application/javascript; charset=UTF-8";
        }

        if (fileName.endsWith(".json")) {

            return "application/json; charset=UTF-8";
        }

        if (fileName.endsWith(".png")) {

            return "image/png";
        }

        if (fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")) {

            return "image/jpeg";
        }

        if (fileName.endsWith(".svg")) {

            return "image/svg+xml";
        }

        if (fileName.endsWith(".ico")) {

            return "image/x-icon";
        }

        return "application/octet-stream";
    }


    // ==================================================
    // TEXT RESPONSE
    // ==================================================

    private static void sendText(
            HttpExchange exchange,
            int statusCode,
            String message)
            throws IOException {

        byte[] response =
                message.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/plain; charset=UTF-8"
                );


        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );


        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(response);
        }
    }
}