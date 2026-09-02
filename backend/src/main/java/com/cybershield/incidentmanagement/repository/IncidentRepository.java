package com.cybershield.incidentmanagement.repository;

import com.cybershield.incidentmanagement.database.DatabaseConnection;
import com.cybershield.incidentmanagement.entity.Incident;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class IncidentRepository {

    // =============================
    // CREATE
    // =============================

    public boolean save(Incident incident) {

        String sql = """
                INSERT INTO incidents
                (
                    title,
                    description,
                    category,
                    severity,
                    status,
                    reported_by
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setString(
                    1,
                    incident.getTitle()
            );

            statement.setString(
                    2,
                    incident.getDescription()
            );

            statement.setString(
                    3,
                    incident.getCategory()
            );

            statement.setString(
                    4,
                    incident.getSeverity()
            );

            statement.setString(
                    5,
                    incident.getStatus()
            );

            statement.setInt(
                    6,
                    incident.getReportedBy()
            );

            int rows = statement.executeUpdate();

            if (rows > 0) {
                ResultSet rs = statement.getGeneratedKeys();
                if (rs.next()) {
                    incident.setId(rs.getInt(1));
                }
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // READ ONE
    // =============================

    public Incident findById(int id) {

        String sql = """
                SELECT
                    i.id,
                    i.title,
                    i.description,
                    i.category,
                    i.severity,
                    i.status,
                    i.reported_by,
                    i.assigned_to,
                    i.created_at,
                    i.updated_at,
                    r.name AS reported_by_name,
                    a.name AS assigned_to_name
                FROM incidents i
                LEFT JOIN users r ON i.reported_by = r.id
                LEFT JOIN users a ON i.assigned_to = a.id
                WHERE i.id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return mapIncident(resultSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =============================
    // READ ALL
    // =============================

    public List<Incident> findAll() {

        List<Incident> incidents = new ArrayList<>();

        String sql = """
                SELECT
                    i.id,
                    i.title,
                    i.description,
                    i.category,
                    i.severity,
                    i.status,
                    i.reported_by,
                    i.assigned_to,
                    i.created_at,
                    i.updated_at,
                    r.name AS reported_by_name,
                    a.name AS assigned_to_name
                FROM incidents i
                LEFT JOIN users r ON i.reported_by = r.id
                LEFT JOIN users a ON i.assigned_to = a.id
                ORDER BY i.id DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                incidents.add(mapIncident(resultSet));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return incidents;
    }

    // =============================
    // UPDATE
    // =============================

    public boolean update(Incident incident) {

        String sql = """
                UPDATE incidents
                SET
                    title = ?,
                    description = ?,
                    category = ?,
                    severity = ?,
                    status = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, incident.getTitle());
            statement.setString(2, incident.getDescription());
            statement.setString(3, incident.getCategory());
            statement.setString(4, incident.getSeverity());
            statement.setString(5, incident.getStatus());
            statement.setInt(6, incident.getId());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // UPDATE ASSIGNMENT
    // =============================

    public boolean updateAssignment(int id, Integer assignedTo) {

        String sql = """
                UPDATE incidents
                SET assigned_to = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            if (assignedTo == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, assignedTo);
            }

            statement.setInt(2, id);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // DELETE
    // =============================

    public boolean delete(int id) {

        String sql = "DELETE FROM incidents WHERE id = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // MAP DATABASE ROW
    // =============================

    private Incident mapIncident(ResultSet resultSet) throws Exception {

        Incident incident = new Incident();

        incident.setId(resultSet.getInt("id"));
        incident.setTitle(resultSet.getString("title"));
        incident.setDescription(resultSet.getString("description"));
        incident.setCategory(resultSet.getString("category"));
        incident.setSeverity(resultSet.getString("severity"));
        incident.setStatus(resultSet.getString("status"));
        incident.setReportedBy(resultSet.getInt("reported_by"));

        int assignedTo = resultSet.getInt("assigned_to");
        if (!resultSet.wasNull()) {
            incident.setAssignedTo(assignedTo);
        }

        incident.setReportedByName(resultSet.getString("reported_by_name"));
        incident.setAssignedToName(resultSet.getString("assigned_to_name"));

        incident.setCreatedAt(resultSet.getTimestamp("created_at"));
        incident.setUpdatedAt(resultSet.getTimestamp("updated_at"));

        return incident;
    }
}