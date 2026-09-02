package com.cybershield.incidentmanagement.repository;

import com.cybershield.incidentmanagement.entity.Incident;

public class IncidentRepositoryTest {

    public static void main(String[] args) {

        IncidentRepository repository =
                new IncidentRepository();


        Incident incident =
                new Incident(
                        "Suspicious Login",
                        "Multiple failed login attempts detected.",
                        "SUSPICIOUS_ACTIVITY",
                        "MEDIUM",
                        1
                );


        boolean saved =
                repository.save(incident);


        if (saved) {

            System.out.println(
                    "Incident saved successfully."
            );

        } else {

            System.out.println(
                    "Incident save failed."
            );
        }
    }
}