package com.cybershield.incidentmanagement.database;

import java.sql.Connection;

public class DatabaseTest {

    public static void main(String[] args) {

        try {

            Connection connection =
                    DatabaseConnection.getConnection();

            System.out.println("--------------------------------");
            System.out.println("DATABASE CONNECTION SUCCESSFUL");
            System.out.println("--------------------------------");

            System.out.println(
                    "Database: " +
                    connection.getCatalog()
            );

            connection.close();

        } catch (Exception e) {

            System.out.println("--------------------------------");
            System.out.println("DATABASE CONNECTION FAILED");
            System.out.println("--------------------------------");

            e.printStackTrace();
        }
    }
}