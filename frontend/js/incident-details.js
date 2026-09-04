const API_URL = "https://cybershield-xe22.onrender.com";

const sessionId = localStorage.getItem("sessionId");
let userRole = localStorage.getItem("userRole");

// =============================
// CHECK LOGIN
// =============================

if (!sessionId) {
    window.location.href = "../login.html";
}

// =============================
// GET INCIDENT ID
// =============================

const params = new URLSearchParams(window.location.search);
const incidentId = params.get("id");

const message = document.getElementById("message");
const editButton = document.getElementById("editButton");
const assignmentSection = document.getElementById("assignmentSection");
const assignUserSelect = document.getElementById("assignUserSelect");
const assignButton = document.getElementById("assignButton");
const unassignButton = document.getElementById("unassignButton");
const activityLogSection = document.getElementById("activityLogSection");
const activityLogTableBody = document.getElementById("activityLogTableBody");

// =============================
// CHECK ID & START
// =============================

if (!incidentId) {
    if (message) message.textContent = "Incident ID not found.";
    if (editButton) editButton.style.display = "none";
} else {
    initIncidentDetailsPage();
}

async function initIncidentDetailsPage() {
    await fetchUserProfile();
    await loadIncident(incidentId);
    await loadActivityLog(incidentId);
}

// =============================
// FETCH USER PROFILE & ROLE
// =============================

async function fetchUserProfile() {
    try {
        const response = await fetch(API_URL + "/api/profile", {
            method: "GET",
            headers: {
                "Session-Id": sessionId
            }
        });

        if (response.ok) {
            const user = await response.json();
            userRole = user.role;
            if (userRole) {
                localStorage.setItem("userRole", userRole);
            }
        }
    } catch (error) {
        console.error("Error fetching profile:", error);
    }
}

// =============================
// LOAD INCIDENT
// =============================

async function loadIncident(id) {
    try {
        const response = await fetch(API_URL + "/api/incidents/" + id, {
            method: "GET",
            headers: {
                "Session-Id": sessionId
            }
        });

        if (!response.ok) {
            if (message) message.textContent = "Incident not found.";
            if (editButton) editButton.style.display = "none";
            return;
        }

        const incident = await response.json();

        // =============================
        // DISPLAY DATA
        // =============================

        document.getElementById("incidentId").textContent = incident.id;
        document.getElementById("incidentTitle").textContent = incident.title;
        document.getElementById("incidentCategory").textContent = incident.category;
        document.getElementById("incidentSeverity").textContent = incident.severity;
        document.getElementById("incidentStatus").textContent = incident.status;

        const reporterText = incident.reportedByName
            ? `${incident.reportedByName} (ID: ${incident.reportedBy})`
            : `User #${incident.reportedBy}`;
        document.getElementById("incidentReporter").textContent = reporterText;

        const assigneeText = incident.assignedToName
            ? `${incident.assignedToName} (ID: ${incident.assignedTo})`
            : (incident.assignedTo ? `User #${incident.assignedTo}` : "Unassigned");
        document.getElementById("incidentAssignee").textContent = assigneeText;

        document.getElementById("incidentDescription").textContent = incident.description;

        // =============================
        // DATE / TIME
        // =============================

        if (incident.createdAt) {
            document.getElementById("createdAt").textContent = formatDate(incident.createdAt);
        } else {
            document.getElementById("createdAt").textContent = "-";
        }

        if (incident.updatedAt) {
            document.getElementById("updatedAt").textContent = formatDate(incident.updatedAt);
        } else {
            document.getElementById("updatedAt").textContent = "-";
        }

        // =============================
        // ADMIN ASSIGNMENT SECTION
        // =============================

        const isAdmin = userRole && userRole.toUpperCase() === "ADMIN";

        if (isAdmin) {
            if (assignmentSection) assignmentSection.style.display = "block";
            await populateUsersDropdown(incident.assignedTo);
        } else {
            if (assignmentSection) assignmentSection.style.display = "none";
            if (editButton) editButton.style.display = "none";
        }

    } catch (error) {
        console.error(error);
        if (message) message.textContent = "Cannot connect to Java server.";
    }
}

// =============================
// POPULATE USERS DROPDOWN (ADMIN)
// =============================

async function populateUsersDropdown(currentAssignedToId) {
    if (!assignUserSelect) return;

    try {
        const response = await fetch(API_URL + "/api/users", {
            method: "GET",
            headers: {
                "Session-Id": sessionId
            }
        });

        if (!response.ok) return;

        const users = await response.json();

        assignUserSelect.innerHTML = '<option value="">-- Select User --</option>';

        users.forEach(user => {
            const option = document.createElement("option");
            option.value = user.id;
            option.textContent = `${user.name} (${user.email} - ${user.role})`;

            if (currentAssignedToId && Number(user.id) === Number(currentAssignedToId)) {
                option.selected = true;
            }

            assignUserSelect.appendChild(option);
        });

    } catch (error) {
        console.error("Error loading users for assignment:", error);
    }
}

// =============================
// ASSIGN BUTTON EVENT
// =============================

if (assignButton) {
    assignButton.addEventListener("click", async function() {
        const selectedUserId = assignUserSelect.value;

        if (!selectedUserId) {
            if (message) {
                message.textContent = "Please select a user to assign.";
                message.style.color = "red";
            }
            return;
        }

        try {
            const response = await fetch(API_URL + "/api/incidents/" + incidentId + "/assignment", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Session-Id": sessionId
                },
                body: JSON.stringify({
                    assignedTo: parseInt(selectedUserId)
                })
            });

            const data = await response.json();

            if (response.ok) {
                if (message) {
                    message.textContent = data.message || "Incident assigned successfully.";
                    message.style.color = "green";
                }
                await loadIncident(incidentId);
                await loadActivityLog(incidentId);
            } else {
                if (message) {
                    message.textContent = data.message || "Failed to assign incident.";
                    message.style.color = "red";
                }
            }
        } catch (error) {
            console.error("Assignment error:", error);
            if (message) {
                message.textContent = "Error connecting to server.";
                message.style.color = "red";
            }
        }
    });
}

// =============================
// UNASSIGN BUTTON EVENT
// =============================

if (unassignButton) {
    unassignButton.addEventListener("click", async function() {
        try {
            const response = await fetch(API_URL + "/api/incidents/" + incidentId + "/assignment", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Session-Id": sessionId
                },
                body: JSON.stringify({
                    assignedTo: null
                })
            });

            const data = await response.json();

            if (response.ok) {
                if (message) {
                    message.textContent = data.message || "Incident unassigned successfully.";
                    message.style.color = "green";
                }
                await loadIncident(incidentId);
                await loadActivityLog(incidentId);
            } else {
                if (message) {
                    message.textContent = data.message || "Failed to unassign incident.";
                    message.style.color = "red";
                }
            }
        } catch (error) {
            console.error("Unassignment error:", error);
            if (message) {
                message.textContent = "Error connecting to server.";
                message.style.color = "red";
            }
        }
    });
}

// =============================
// LOAD ACTIVITY LOG (BATCH 8 ROUTE)
// =============================

async function loadActivityLog(id) {
    if (!activityLogTableBody) return;

    try {
        const response = await fetch(API_URL + "/api/incidents/" + id + "/activity", {
            method: "GET",
            headers: {
                "Session-Id": sessionId
            }
        });

        if (!response.ok) {
            activityLogTableBody.innerHTML = `
                <tr>
                    <td colspan="4">No activity history available.</td>
                </tr>
            `;
            return;
        }

        const logs = await response.json();

        if (logs.length === 0) {
            activityLogTableBody.innerHTML = `
                <tr>
                    <td colspan="4">No activity recorded for this incident yet.</td>
                </tr>
            `;
            return;
        }

        activityLogTableBody.innerHTML = "";

        logs.forEach(log => {
            const row = document.createElement("tr");

            const performedBy = log.userName ? `${log.userName}` : `User #${log.userId}`;

            row.innerHTML = `
                <td><strong>${log.action}</strong></td>
                <td>${performedBy}</td>
                <td>${log.description || "-"}</td>
                <td>${formatDate(log.createdAt)}</td>
            `;

            activityLogTableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Error loading activity log:", error);
        activityLogTableBody.innerHTML = `
            <tr>
                <td colspan="4">Activity log will load once audit API is active.</td>
            </tr>
        `;
    }
}

// =============================
// FORMAT DATE
// =============================

function formatDate(dateValue) {
    if (!dateValue) return "-";
    const date = new Date(dateValue);

    if (isNaN(date.getTime())) {
        return dateValue;
    }

    return date.toLocaleString();
}

// =============================
// EDIT BUTTON
// =============================

if (editButton) {
    editButton.addEventListener("click", function() {
        window.location.href = "create-incident.html?id=" + incidentId;
    });
}

// =============================
// LOGOUT
// =============================

const logoutButton = document.getElementById("logoutButton");

if (logoutButton) {
    logoutButton.addEventListener("click", async function(event) {
        event.preventDefault();

        try {
            await fetch(API_URL + "/api/logout", {
                method: "POST",
                headers: {
                    "Session-Id": sessionId
                }
            });
        } catch (error) {
            console.error(error);
        }

        localStorage.removeItem("sessionId");
        localStorage.removeItem("userEmail");
        localStorage.removeItem("userRole");

        window.location.href = "../login.html";
    });
}