package com.cybershield.incidentmanagement.entity;

import java.sql.Timestamp;

public class Incident {

    private int id;
    private String title;
    private String description;
    private String category;
    private String severity;
    private String status;
    private int reportedBy;
    private Integer assignedTo;
    private String reportedByName;
    private String assignedToName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // =============================
    // DEFAULT CONSTRUCTOR
    // =============================

    public Incident() {
    }

    // =============================
    // CREATE CONSTRUCTOR
    // =============================

    public Incident(
            String title,
            String description,
            String category,
            String severity,
            int reportedBy) {

        this.title = title;
        this.description = description;
        this.category = category;
        this.severity = severity;
        this.status = "OPEN";
        this.reportedBy = reportedBy;
    }

    // =============================
    // ID
    // =============================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // =============================
    // TITLE
    // =============================

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // =============================
    // DESCRIPTION
    // =============================

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // =============================
    // CATEGORY
    // =============================

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // =============================
    // SEVERITY
    // =============================

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    // =============================
    // STATUS
    // =============================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // =============================
    // REPORTED BY
    // =============================

    public int getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(int reportedBy) {
        this.reportedBy = reportedBy;
    }

    // =============================
    // ASSIGNED TO
    // =============================

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    // =============================
    // REPORTED BY NAME
    // =============================

    public String getReportedByName() {
        return reportedByName;
    }

    public void setReportedByName(String reportedByName) {
        this.reportedByName = reportedByName;
    }

    // =============================
    // ASSIGNED TO NAME
    // =============================

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }

    // =============================
    // CREATED AT
    // =============================

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // =============================
    // UPDATED AT
    // =============================

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}