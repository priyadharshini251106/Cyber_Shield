package com.cybershield.incidentmanagement.controller;

import com.cybershield.incidentmanagement.entity.ActivityLog;
import com.cybershield.incidentmanagement.entity.Incident;
import com.cybershield.incidentmanagement.entity.User;
import com.cybershield.incidentmanagement.repository.UserRepository;
import com.cybershield.incidentmanagement.service.IncidentService;
import com.cybershield.incidentmanagement.service.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IncidentController {

    private final IncidentService service = new IncidentService();
    private final UserRepository userRepository = new UserRepository();

    // ==========================================
    // MAIN HANDLER
    // ==========================================

    public void handle(HttpExchange exchange) throws IOException {

        addCorsHeaders(exchange);

        // ==========================================
        // OPTIONS
        // ==========================================

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // ==========================================
        // GET SESSION ID
        // ==========================================

        String sessionId = exchange.getRequestHeaders().getFirst("Session-Id");

        // ==========================================
        // SESSION CHECK
        // ==========================================

        if (!SessionManager.isValid(sessionId)) {
            sendResponse(
                    exchange,
                    401,
                    "{\"message\":\"Unauthorized. Please login.\"}"
            );
            return;
        }

        /*
         * /api/incidents
         */

        if (path.equals("/api/incidents")) {

            // CREATE
            if (method.equalsIgnoreCase("POST")) {
                createIncident(exchange);
                return;
            }

            // GET ALL
            if (method.equalsIgnoreCase("GET")) {
                getAllIncidents(exchange);
                return;
            }
        }

        /*
         * /api/incidents/{id}
         */

        if (path.startsWith("/api/incidents/")) {

            // ======================================
            // ACTIVITY ENDPOINT: /api/incidents/{id}/activity
            // ======================================

            if (path.endsWith("/activity") && method.equalsIgnoreCase("GET")) {
                String basePath = path.substring(0, path.length() - "/activity".length());
                String baseIdText = basePath.substring("/api/incidents/".length());

                int incidentId;
                try {
                    incidentId = Integer.parseInt(baseIdText);
                } catch (Exception e) {
                    sendResponse(
                            exchange,
                            400,
                            "{\"message\":\"Invalid incident ID\"}"
                    );
                    return;
                }

                getIncidentActivity(exchange, incidentId);
                return;
            }

            // ======================================
            // ASSIGNMENT ENDPOINT: /api/incidents/{id}/assignment
            // ======================================

            if (path.endsWith("/assignment") && method.equalsIgnoreCase("PUT")) {

                String basePath = path.substring(0, path.length() - "/assignment".length());
                String baseIdText = basePath.substring("/api/incidents/".length());

                int incidentId;
                try {
                    incidentId = Integer.parseInt(baseIdText);
                } catch (Exception e) {
                    sendResponse(
                            exchange,
                            400,
                            "{\"message\":\"Invalid incident ID\"}"
                    );
                    return;
                }

                // Server-side ADMIN check for Assignment
                if (!SessionManager.isAdmin(sessionId)) {
                    sendResponse(
                            exchange,
                            403,
                            "{\"message\":\"Access denied. Admin only.\"}"
                    );
                    return;
                }

                updateAssignment(exchange, incidentId);
                return;
            }

            String idText = path.substring("/api/incidents/".length());

            int id;

            try {
                id = Integer.parseInt(idText);
            } catch (Exception e) {
                sendResponse(
                        exchange,
                        400,
                        "{\"message\":\"Invalid incident ID\"}"
                );
                return;
            }

            // GET ONE
            if (method.equalsIgnoreCase("GET")) {
                getIncidentById(exchange, id);
                return;
            }

            // UPDATE (ADMIN ONLY)
            if (method.equalsIgnoreCase("PUT")) {
                if (!SessionManager.isAdmin(sessionId)) {
                    sendResponse(
                            exchange,
                            403,
                            "{\"message\":\"Access denied. Admin only.\"}"
                    );
                    return;
                }

                updateIncident(exchange, id);
                return;
            }

            // DELETE (ADMIN ONLY)
            if (method.equalsIgnoreCase("DELETE")) {
                if (!SessionManager.isAdmin(sessionId)) {
                    sendResponse(
                            exchange,
                            403,
                            "{\"message\":\"Access denied. Admin only.\"}"
                    );
                    return;
                }

                deleteIncident(exchange, id);
                return;
            }
        }

        // ==========================================
        // ENDPOINT NOT FOUND
        // ==========================================

        sendResponse(
                exchange,
                404,
                "{\"message\":\"Endpoint not found\"}"
        );
    }

    // ==========================================
    // GET ACTOR USER ID
    // ==========================================

    private int getActorUserId(HttpExchange exchange) {
        String sessionId = exchange.getRequestHeaders().getFirst("Session-Id");
        if (sessionId != null) {
            String email = SessionManager.getEmail(sessionId);
            if (email != null) {
                User user = userRepository.findByEmail(email);
                if (user != null) {
                    return user.getId();
                }
            }
        }
        return 0;
    }

    // ==========================================
    // CREATE
    // ==========================================

    private void createIncident(HttpExchange exchange) throws IOException {

        String body = readRequestBody(exchange);

        String title = getValue(body, "title");
        String description = getValue(body, "description");
        String category = getValue(body, "category");
        String severity = getValue(body, "severity");
        String reportedByText = getValue(body, "reportedBy");

        int reportedBy;

        try {
            reportedBy = Integer.parseInt(reportedByText.trim());
        } catch (Exception e) {
            sendResponse(
                    exchange,
                    400,
                    "{\"message\":\"Invalid reportedBy value\"}"
            );
            return;
        }

        Incident incident = new Incident(
                title,
                description,
                category,
                severity,
                reportedBy
        );

        int actorUserId = getActorUserId(exchange);
        if (actorUserId <= 0) {
            actorUserId = reportedBy;
        }

        boolean success = service.createIncident(incident, actorUserId);

        if (success) {
            sendResponse(
                    exchange,
                    201,
                    "{\"success\":true,\"message\":\"Incident created successfully\"}"
            );
        } else {
            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Incident creation failed\"}"
            );
        }
    }

    // ==========================================
    // GET ALL
    // ==========================================

    private void getAllIncidents(HttpExchange exchange) throws IOException {

        List<Incident> incidents = service.getAllIncidents();

        StringBuilder json = new StringBuilder();

        json.append("[");

        for (int i = 0; i < incidents.size(); i++) {
            json.append(incidentToJson(incidents.get(i)));
            if (i < incidents.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, 200, json.toString());
    }

    // ==========================================
    // GET ONE
    // ==========================================

    private void getIncidentById(HttpExchange exchange, int id) throws IOException {

        Incident incident = service.getIncidentById(id);

        if (incident == null) {
            sendResponse(
                    exchange,
                    404,
                    "{\"message\":\"Incident not found\"}"
            );
            return;
        }

        sendResponse(exchange, 200, incidentToJson(incident));
    }

    // ==========================================
    // GET INCIDENT ACTIVITY LOGS
    // ==========================================

    private void getIncidentActivity(HttpExchange exchange, int incidentId) throws IOException {

        List<ActivityLog> logs = service.getActivityLogs(incidentId);

        StringBuilder json = new StringBuilder();
        json.append("[");

        for (int i = 0; i < logs.size(); i++) {
            ActivityLog log = logs.get(i);
            json.append("{");
            json.append("\"id\":").append(log.getId());
            json.append(",\"incidentId\":").append(log.getIncidentId());
            json.append(",\"userId\":").append(log.getUserId());
            json.append(",\"userName\":\"").append(escapeJson(log.getUserName())).append("\"");
            json.append(",\"action\":\"").append(escapeJson(log.getAction())).append("\"");
            json.append(",\"description\":\"").append(escapeJson(log.getDescription())).append("\"");
            json.append(",\"createdAt\":\"").append(escapeJson(log.getCreatedAt() != null ? log.getCreatedAt().toString() : "")).append("\"");
            json.append("}");

            if (i < logs.size() - 1) {
                json.append(",");
            }
        }

        json.append("]");

        sendResponse(exchange, 200, json.toString());
    }

    // ==========================================
    // UPDATE
    // ==========================================

    private void updateIncident(HttpExchange exchange, int id) throws IOException {

        String body = readRequestBody(exchange);

        Incident incident = new Incident();
        incident.setId(id);
        incident.setTitle(getValue(body, "title"));
        incident.setDescription(getValue(body, "description"));
        incident.setCategory(getValue(body, "category"));
        incident.setSeverity(getValue(body, "severity"));
        incident.setStatus(getValue(body, "status"));

        int actorUserId = getActorUserId(exchange);

        boolean success = service.updateIncident(incident, actorUserId);

        if (success) {
            sendResponse(
                    exchange,
                    200,
                    "{\"success\":true,\"message\":\"Incident updated successfully\"}"
            );
        } else {
            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Incident update failed\"}"
            );
        }
    }

    // ==========================================
    // UPDATE ASSIGNMENT
    // ==========================================

    private void updateAssignment(HttpExchange exchange, int id) throws IOException {

        String body = readRequestBody(exchange);
        String assignedToVal = getValue(body, "assignedTo");

        Integer assignedTo = null;

        if (assignedToVal != null && !assignedToVal.isBlank() && !assignedToVal.equalsIgnoreCase("null")) {
            try {
                assignedTo = Integer.parseInt(assignedToVal.trim());
            } catch (Exception e) {
                sendResponse(
                        exchange,
                        400,
                        "{\"message\":\"Invalid assignedTo value\"}"
                );
                return;
            }
        }

        int actorUserId = getActorUserId(exchange);

        boolean success = service.updateAssignment(id, assignedTo, actorUserId);

        if (success) {
            sendResponse(
                    exchange,
                    200,
                    "{\"success\":true,\"message\":\"Incident assignment updated successfully\"}"
            );
        } else {
            sendResponse(
                    exchange,
                    400,
                    "{\"success\":false,\"message\":\"Incident assignment failed\"}"
            );
        }
    }

    // ==========================================
    // DELETE
    // ==========================================

    private void deleteIncident(HttpExchange exchange, int id) throws IOException {

        int actorUserId = getActorUserId(exchange);

        boolean success = service.deleteIncident(id, actorUserId);

        if (success) {
            sendResponse(
                    exchange,
                    200,
                    "{\"success\":true,\"message\":\"Incident deleted successfully\"}"
            );
        } else {
            sendResponse(
                    exchange,
                    404,
                    "{\"success\":false,\"message\":\"Incident not found\"}"
            );
        }
    }

    // ==========================================
    // INCIDENT → JSON
    // ==========================================

    private String incidentToJson(Incident incident) {

        StringBuilder json = new StringBuilder();

        json.append("{");

        // ID
        json.append("\"id\":").append(incident.getId());

        // TITLE
        json.append(",");
        json.append("\"title\":\"").append(escapeJson(incident.getTitle())).append("\"");

        // DESCRIPTION
        json.append(",");
        json.append("\"description\":\"").append(escapeJson(incident.getDescription())).append("\"");

        // CATEGORY
        json.append(",");
        json.append("\"category\":\"").append(escapeJson(incident.getCategory())).append("\"");

        // SEVERITY
        json.append(",");
        json.append("\"severity\":\"").append(escapeJson(incident.getSeverity())).append("\"");

        // STATUS
        json.append(",");
        json.append("\"status\":\"").append(escapeJson(incident.getStatus())).append("\"");

        // REPORTED BY
        json.append(",");
        json.append("\"reportedBy\":").append(incident.getReportedBy());

        // REPORTED BY NAME
        json.append(",");
        if (incident.getReportedByName() != null) {
            json.append("\"reportedByName\":\"").append(escapeJson(incident.getReportedByName())).append("\"");
        } else {
            json.append("\"reportedByName\":null");
        }

        // ASSIGNED TO
        json.append(",");
        if (incident.getAssignedTo() != null) {
            json.append("\"assignedTo\":").append(incident.getAssignedTo());
        } else {
            json.append("\"assignedTo\":null");
        }

        // ASSIGNED TO NAME
        json.append(",");
        if (incident.getAssignedToName() != null) {
            json.append("\"assignedToName\":\"").append(escapeJson(incident.getAssignedToName())).append("\"");
        } else {
            json.append("\"assignedToName\":null");
        }

        // CREATED AT
        json.append(",");
        if (incident.getCreatedAt() != null) {
            json.append("\"createdAt\":\"").append(escapeJson(incident.getCreatedAt().toString())).append("\"");
        } else {
            json.append("\"createdAt\":null");
        }

        // UPDATED AT
        json.append(",");
        if (incident.getUpdatedAt() != null) {
            json.append("\"updatedAt\":\"").append(escapeJson(incident.getUpdatedAt().toString())).append("\"");
        } else {
            json.append("\"updatedAt\":null");
        }

        json.append("}");

        return json.toString();
    }

    // ==========================================
    // READ REQUEST BODY
    // ==========================================

    private String readRequestBody(HttpExchange exchange) throws IOException {

        InputStream inputStream = exchange.getRequestBody();

        return new String(
                inputStream.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    // ==========================================
    // GET JSON VALUE
    // ==========================================

    private String getValue(String json, String key) {

        String search = "\"" + key + "\"";

        int keyPosition = json.indexOf(search);

        if (keyPosition == -1) {
            return "";
        }

        int colonPosition = json.indexOf(":", keyPosition);

        if (colonPosition == -1) {
            return "";
        }

        int start = colonPosition + 1;

        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (start < json.length() && json.charAt(start) == '"') {

            start++;

            int end = json.indexOf("\"", start);

            if (end == -1) {
                return "";
            }

            return json.substring(start, end);
        }

        int end = start;

        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }

        return json.substring(start, end).trim();
    }

    // ==========================================
    // ESCAPE JSON
    // ==========================================

    private String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // ==========================================
    // CORS
    // ==========================================

    private void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin",
                "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers",
                "Content-Type,Session-Id"
        );
    }

    // ==========================================
    // RESPONSE
    // ==========================================

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
        );

        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        OutputStream outputStream = exchange.getResponseBody();

        outputStream.write(responseBytes);

        outputStream.close();
    }
}