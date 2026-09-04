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

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CyberShieldServer {

    public static void main(String[] args)
            throws IOException {

        /*
         * Render provides the PORT environment variable.
         *
         * Local fallback is 8080 so that this Render version
         * can also be tested locally if required.
         */
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


        /*
         * Initialize database and default admin.
         */
        DatabaseInitializer.initialize();


        /*
         * Render requires the server to listen on
         * 0.0.0.0 instead of localhost.
         */
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
        // Routes
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
                "CyberShield API is running"
        );

        System.out.println(
                "------------------------------------------"
        );
    }
}