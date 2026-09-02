package com.cybershield.incidentmanagement.repository;

import com.cybershield.incidentmanagement.database.DatabaseConnection;
import com.cybershield.incidentmanagement.entity.ActivityLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogRepository {

    // =============================
    // CREATE LOG
    // =============================

    public boolean save(ActivityLog log) {

        String sql = """
                INSERT INTO activity_logs
                (incident_id, user_id, action, description)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            if (log.getIncidentId() == null) {
                statement.setNull(1, Types.INTEGER);
            } else {
                statement.setInt(1, log.getIncidentId());
            }

            statement.setInt(2, log.getUserId());
            statement.setString(3, log.getAction());
            statement.setString(4, log.getDescription());

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // READ LOGS BY INCIDENT ID
    // =============================

    public List<ActivityLog> findByIncidentId(int incidentId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT
                    a.id,
                    a.incident_id,
                    a.user_id,
                    u.name AS user_name,
                    a.action,
                    a.description,
                    a.created_at
                FROM activity_logs a
                LEFT JOIN users u ON a.user_id = u.id
                WHERE a.incident_id = ?
                ORDER BY a.id ASC
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, incidentId);

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));

                int incId = rs.getInt("incident_id");
                if (!rs.wasNull()) {
                    log.setIncidentId(incId);
                }

                log.setUserId(rs.getInt("user_id"));
                log.setUserName(rs.getString("user_name"));
                log.setAction(rs.getString("action"));
                log.setDescription(rs.getString("description"));
                log.setCreatedAt(rs.getTimestamp("created_at"));

                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }
}
