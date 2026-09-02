package com.cybershield.incidentmanagement.entity;

import java.sql.Timestamp;

public class ActivityLog {

    private int id;
    private Integer incidentId;
    private int userId;
    private String userName;
    private String action;
    private String description;
    private Timestamp createdAt;

    public ActivityLog() {
    }

    public ActivityLog(Integer incidentId, int userId, String action, String description) {
        this.incidentId = incidentId;
        this.userId = userId;
        this.action = action;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(Integer incidentId) {
        this.incidentId = incidentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
