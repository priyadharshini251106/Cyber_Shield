package com.cybershield.incidentmanagement.service;

import com.cybershield.incidentmanagement.entity.ActivityLog;
import com.cybershield.incidentmanagement.entity.Incident;
import com.cybershield.incidentmanagement.entity.User;
import com.cybershield.incidentmanagement.repository.ActivityLogRepository;
import com.cybershield.incidentmanagement.repository.IncidentRepository;
import com.cybershield.incidentmanagement.repository.UserRepository;

import java.util.List;

public class IncidentService {

    private final IncidentRepository repository = new IncidentRepository();
    private final ActivityLogRepository activityLogRepository = new ActivityLogRepository();
    private final UserRepository userRepository = new UserRepository();

    // =============================
    // CREATE
    // =============================

    public boolean createIncident(Incident incident, int actorUserId) {

        if (incident.getTitle() == null || incident.getTitle().isBlank()) {
            return false;
        }

        if (incident.getDescription() == null || incident.getDescription().isBlank()) {
            return false;
        }

        if (incident.getCategory() == null || incident.getCategory().isBlank()) {
            return false;
        }

        if (incident.getSeverity() == null || incident.getSeverity().isBlank()) {
            return false;
        }

        boolean saved = repository.save(incident);

        if (saved) {
            int userId = actorUserId > 0 ? actorUserId : incident.getReportedBy();
            ActivityLog log = new ActivityLog(incident.getId(), userId, "CREATED", "Incident created");
            activityLogRepository.save(log);
        }

        return saved;
    }

    public boolean createIncident(Incident incident) {
        return createIncident(incident, incident.getReportedBy());
    }

    // =============================
    // READ ONE
    // =============================

    public Incident getIncidentById(int id) {

        if (id <= 0) {
            return null;
        }

        return repository.findById(id);
    }

    // =============================
    // READ ALL
    // =============================

    public List<Incident> getAllIncidents() {
        return repository.findAll();
    }

    // =============================
    // UPDATE
    // =============================

    public boolean updateIncident(Incident incident, int actorUserId) {

        if (incident.getId() <= 0) {
            return false;
        }

        if (incident.getTitle() == null || incident.getTitle().isBlank()) {
            return false;
        }

        if (incident.getDescription() == null || incident.getDescription().isBlank()) {
            return false;
        }

        if (incident.getCategory() == null || incident.getCategory().isBlank()) {
            return false;
        }

        if (incident.getSeverity() == null || incident.getSeverity().isBlank()) {
            return false;
        }

        if (incident.getStatus() == null || incident.getStatus().isBlank()) {
            return false;
        }

        Incident existing = repository.findById(incident.getId());
        boolean updated = repository.update(incident);

        if (updated && existing != null) {
            String desc = "Incident updated";
            if (!existing.getStatus().equalsIgnoreCase(incident.getStatus())) {
                desc = "Status changed from " + existing.getStatus() + " to " + incident.getStatus();
            } else if (!existing.getSeverity().equalsIgnoreCase(incident.getSeverity())) {
                desc = "Severity changed from " + existing.getSeverity() + " to " + incident.getSeverity();
            }

            int userId = actorUserId > 0 ? actorUserId : existing.getReportedBy();
            ActivityLog log = new ActivityLog(incident.getId(), userId, "UPDATED", desc);
            activityLogRepository.save(log);
        }

        return updated;
    }

    public boolean updateIncident(Incident incident) {
        return updateIncident(incident, 0);
    }

    // =============================
    // UPDATE ASSIGNMENT
    // =============================

    public boolean updateAssignment(int id, Integer assignedTo, int actorUserId) {

        if (id <= 0) {
            return false;
        }

        boolean updated = repository.updateAssignment(id, assignedTo);

        if (updated) {
            String action;
            String desc;
            if (assignedTo != null) {
                action = "ASSIGNED";
                User assignedUser = userRepository.findById(assignedTo);
                String assigneeName = assignedUser != null ? assignedUser.getName() : ("User #" + assignedTo);
                desc = "Assigned to " + assigneeName;
            } else {
                action = "UNASSIGNED";
                desc = "Incident unassigned";
            }

            ActivityLog log = new ActivityLog(id, actorUserId, action, desc);
            activityLogRepository.save(log);
        }

        return updated;
    }

    public boolean updateAssignment(int id, Integer assignedTo) {
        return updateAssignment(id, assignedTo, 0);
    }

    // =============================
    // DELETE
    // =============================

    public boolean deleteIncident(int id, int actorUserId) {

        if (id <= 0) {
            return false;
        }

        if (actorUserId > 0) {
            ActivityLog log = new ActivityLog(id, actorUserId, "DELETED", "Incident deleted");
            activityLogRepository.save(log);
        }

        return repository.delete(id);
    }

    public boolean deleteIncident(int id) {
        return deleteIncident(id, 0);
    }

    // =============================
    // ACTIVITY LOGS
    // =============================

    public List<ActivityLog> getActivityLogs(int incidentId) {
        return activityLogRepository.findByIncidentId(incidentId);
    }
}