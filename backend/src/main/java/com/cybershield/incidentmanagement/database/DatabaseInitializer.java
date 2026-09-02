package com.cybershield.incidentmanagement.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    // ==========================================
    // DEFAULT ADMIN CREDENTIALS
    // ==========================================

    private static final String ADMIN_NAME =
            "CyberShield Admin";

    private static final String ADMIN_EMAIL =
            "admin@cybershield.com";

    private static final String ADMIN_PASSWORD =
            "Admin@12345";

    private static final String ADMIN_ROLE =
            "ADMIN";


    // ==========================================
    // INITIALIZE DATABASE TABLES & DEFAULT ADMIN
    // ==========================================

    public static void initialize() {

        createActivityLogsTable();
        createDefaultAdmin();
    }


    private static void createActivityLogsTable() {

        String sql = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    incident_id INT NULL,
                    user_id INT NOT NULL,
                    action VARCHAR(100) NOT NULL,
                    description VARCHAR(500),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (incident_id) REFERENCES incidents(id)
                        ON DELETE SET NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.execute(sql);
            System.out.println("Activity logs table checked/created.");
        } catch (Exception e) {
            System.out.println("Unable to initialize activity_logs table.");
            e.printStackTrace();
        }
    }


    private static void createDefaultAdmin() {

        String checkSql = """
                SELECT id
                FROM users
                WHERE email = ?
                """;


        String insertSql = """
                INSERT INTO users
                (name, email, password, role, status)
                VALUES (?, ?, ?, ?, TRUE)
                """;


        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement checkStatement =
                        connection.prepareStatement(
                                checkSql
                        )
        ) {

            checkStatement.setString(
                    1,
                    ADMIN_EMAIL
            );


            ResultSet resultSet =
                    checkStatement.executeQuery();


            if (resultSet.next()) {

                System.out.println(
                        "Default admin account already exists."
                );

                return;
            }


            try (
                    PreparedStatement insertStatement =
                            connection.prepareStatement(
                                    insertSql
                            )
            ) {

                insertStatement.setString(
                        1,
                        ADMIN_NAME
                );

                insertStatement.setString(
                        2,
                        ADMIN_EMAIL
                );

                insertStatement.setString(
                        3,
                        ADMIN_PASSWORD
                );

                insertStatement.setString(
                        4,
                        ADMIN_ROLE
                );


                int rows =
                        insertStatement.executeUpdate();


                if (rows > 0) {

                    System.out.println(
                            "================================="
                    );

                    System.out.println(
                            "Default ADMIN account created."
                    );

                    System.out.println(
                            "Email: "
                            + ADMIN_EMAIL
                    );

                    System.out.println(
                            "Password: "
                            + ADMIN_PASSWORD
                    );

                    System.out.println(
                            "Role: "
                            + ADMIN_ROLE
                    );

                    System.out.println(
                            "================================="
                    );

                }

            }


        } catch (Exception e) {

            System.out.println(
                    "Unable to initialize admin account."
            );

            e.printStackTrace();

        }

    }

}