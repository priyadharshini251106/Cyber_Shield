const API_URL =
    "http://localhost:8080";


// =============================
// CURRENT USER / VIEW
// =============================

let currentUserId = null;

let currentView = "all";

let allIncidents = [];


// =============================
// HTML ELEMENTS
// =============================

const tableBody =
    document.getElementById(
        "incidentTableBody"
    );


const message =
    document.getElementById(
        "message"
    );


const sessionId =
    localStorage.getItem(
        "sessionId"
    );
const userRole =
    localStorage.getItem(
        "userRole"
    );


// =============================
// SESSION CHECK
// =============================

if (!sessionId) {

    window.location.href =
        "../login.html";

}


// =============================
// SEARCH AND FILTER ELEMENTS
// =============================

const searchInput =
    document.getElementById(
        "searchInput"
    );


const severityFilter =
    document.getElementById(
        "severityFilter"
    );


const statusFilter =
    document.getElementById(
        "statusFilter"
    );


const resultCount =
    document.getElementById(
        "resultCount"
    );


// =============================
// VIEW BUTTONS
// =============================

const allIncidentsButton =
    document.getElementById(
        "allIncidentsButton"
    );


const myIncidentsButton =
    document.getElementById(
        "myIncidentsButton"
    );


// =============================
// LOAD CURRENT USER
// =============================

async function loadCurrentUser() {

    try {

        const response =
            await fetch(
                API_URL +
                "/api/profile",
                {
                    method: "GET",

                    headers: {
                        "Session-Id":
                            sessionId
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to load profile"
            );

        }


        const user =
            await response.json();


        currentUserId =
            user.id;


        console.log(
            "Current user ID:",
            currentUserId
        );


    } catch (error) {

        console.error(
            "Profile loading error:",
            error
        );

    }

}


// =============================
// LOAD INCIDENTS
// =============================

async function loadIncidents() {

    try {

        const response =
            await fetch(
                API_URL +
                "/api/incidents",
                {
                    method: "GET",

                    headers: {
                        "Session-Id":
                            sessionId
                    }
                }
            );


        if (!response.ok) {

            message.textContent =
                "Unable to load incidents.";

            return;

        }


        allIncidents =
            await response.json();


        console.log(
            "All incidents:",
            allIncidents
        );


        filterIncidents();


    } catch (error) {

        console.error(error);


        tableBody.innerHTML = `
            <tr>
                <td colspan="8">
                    Cannot connect to Java server.
                </td>
            </tr>
        `;

    }

}


// =============================
// GET INCIDENTS FOR CURRENT VIEW
// =============================

function getDisplayedIncidents() {

    // ALL INCIDENTS
    if (currentView === "all") {
        return allIncidents;
    }

    // MY INCIDENTS
    return allIncidents.filter(
        function(incident) {
            return Number(
                incident.reportedBy
            ) === Number(
                currentUserId
            );
        }
    );

}


// =============================
// FILTER INCIDENTS
// =============================

function filterIncidents() {

    const searchText =
        searchInput.value
            .trim()
            .toLowerCase();


    const selectedSeverity =
        severityFilter.value;


    const selectedStatus =
        statusFilter.value;


    const displayedIncidents =
        getDisplayedIncidents();


    const filteredIncidents =
        displayedIncidents.filter(
            function(incident) {

                const title =
                    incident.title
                        ? incident.title.toLowerCase()
                        : "";

                const category =
                    incident.category
                        ? incident.category.toLowerCase()
                        : "";

                const severity =
                    incident.severity
                        ? incident.severity.toLowerCase()
                        : "";

                const status =
                    incident.status
                        ? incident.status.toLowerCase()
                        : "";

                const reporterName =
                    incident.reportedByName
                        ? incident.reportedByName.toLowerCase()
                        : "";

                const assigneeName =
                    incident.assignedToName
                        ? incident.assignedToName.toLowerCase()
                        : "";

                const searchMatch =
                    title.includes(searchText) ||
                    category.includes(searchText) ||
                    severity.includes(searchText) ||
                    status.includes(searchText) ||
                    reporterName.includes(searchText) ||
                    assigneeName.includes(searchText);

                const severityMatch =
                    selectedSeverity === "ALL" ||
                    incident.severity === selectedSeverity;

                const statusMatch =
                    selectedStatus === "ALL" ||
                    incident.status === selectedStatus;

                return searchMatch && severityMatch && statusMatch;

            }
        );


    displayIncidents(
        filteredIncidents
    );

}


// =============================
// DISPLAY INCIDENTS
// =============================

function displayIncidents(
    incidents
) {

    tableBody.innerHTML = "";


    if (resultCount) {

        resultCount.textContent =
            "Showing " +
            incidents.length +
            " incident(s)";

    }


    if (incidents.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="8">
                    No incidents found.
                </td>
            </tr>
        `;

        return;

    }


    incidents.forEach(
        function(incident) {

            const row =
                document.createElement(
                    "tr"
                );

            let actionButtons = `
                <button
                    onclick="viewIncident(${incident.id})"
                    class="action-button"
                >
                    View
                </button>
            `;

            if (userRole && userRole.toUpperCase() === "ADMIN") {

                actionButtons += `
                    <button
                        onclick="editIncident(${incident.id})"
                        class="action-button"
                    >
                        Edit
                    </button>
                    <button
                        onclick="deleteIncident(${incident.id})"
                        class="delete-button"
                    >
                        Delete
                    </button>
                `;

            }

            const reporter = incident.reportedByName || incident.reportedBy || "-";
            const assignee = incident.assignedToName || (incident.assignedTo ? ("User #" + incident.assignedTo) : "Unassigned");

            row.innerHTML = `
                <td>${incident.id}</td>
                <td>${incident.title}</td>
                <td>${incident.category}</td>
                <td>${incident.severity}</td>
                <td>${incident.status}</td>
                <td>${reporter}</td>
                <td>${assignee}</td>
                <td>${actionButtons}</td>
            `;

            tableBody.appendChild(
                row
            );

        }
    );

}


// =============================
// SEARCH EVENT
// =============================

if (searchInput) {

    searchInput.addEventListener(
        "input",
        filterIncidents
    );

}


// =============================
// SEVERITY FILTER EVENT
// =============================

if (severityFilter) {

    severityFilter.addEventListener(
        "change",
        filterIncidents
    );

}


// =============================
// STATUS FILTER EVENT
// =============================

if (statusFilter) {

    statusFilter.addEventListener(
        "change",
        filterIncidents
    );

}


// =============================
// ALL INCIDENTS BUTTON
// =============================

if (allIncidentsButton) {

    allIncidentsButton.addEventListener(
        "click",
        function() {

            currentView = "all";

            allIncidentsButton.classList.add(
                "active"
            );

            if (myIncidentsButton) {

                myIncidentsButton.classList.remove(
                    "active"
                );

            }

            filterIncidents();

        }
    );

}


// =============================
// MY INCIDENTS BUTTON
// =============================

if (myIncidentsButton) {

    myIncidentsButton.addEventListener(
        "click",
        function() {

            currentView = "my";

            myIncidentsButton.classList.add(
                "active"
            );

            if (allIncidentsButton) {

                allIncidentsButton.classList.remove(
                    "active"
                );

            }

            filterIncidents();

        }
    );

}


// =============================
// VIEW INCIDENT
// =============================

function viewIncident(id) {

    window.location.href =
        "incident-details.html?id=" +
        id;

}


// =============================
// EDIT INCIDENT
// =============================

function editIncident(id) {

    window.location.href =
        "create-incident.html?id=" +
        id;

}


// =============================
// DELETE INCIDENT
// =============================

async function deleteIncident(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this incident?"
        );

    if (!confirmed) {

        return;

    }

    try {

        const response =
            await fetch(
                API_URL +
                "/api/incidents/" +
                id,
                {
                    method: "DELETE",

                    headers: {
                        "Session-Id":
                            sessionId
                    }
                }
            );

        const data =
            await response.json();

        if (message) {

            message.textContent =
                data.message;

        }

        await loadIncidents();

    } catch (error) {

        console.error(error);

        if (message) {

            message.textContent =
                "Unable to delete incident.";

        }

    }

}


// =============================
// LOGOUT
// =============================

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        async function(event) {

            event.preventDefault();

            const currentSessionId =
                localStorage.getItem(
                    "sessionId"
                );

            try {

                await fetch(
                    API_URL +
                    "/api/logout",
                    {
                        method: "POST",

                        headers: {
                            "Session-Id":
                                currentSessionId
                        }
                    }
                );

            } catch (error) {

                console.error(
                    "Logout error:",
                    error
                );

            }

            localStorage.removeItem(
                "sessionId"
            );

            localStorage.removeItem(
                "userEmail"
            );

            localStorage.removeItem(
                "userRole"
            );

            window.location.href =
                "../login.html";

        }
    );

}


// =============================
// ENTER KEY NAVIGATION
// =============================

document.addEventListener(
    "keydown",
    function(event) {

        if (event.key !== "Enter") {

            return;

        }

        const current =
            document.activeElement;

        if (
            current.tagName !== "INPUT" &&
            current.tagName !== "SELECT" &&
            current.tagName !== "TEXTAREA"
        ) {

            return;

        }

        event.preventDefault();

        const fields =
            Array.from(
                document.querySelectorAll(
                    "input, select, textarea"
                )
            ).filter(
                field =>
                    !field.disabled &&
                    field.type !== "hidden"
            );

        const currentIndex =
            fields.indexOf(current);

        if (
            currentIndex >= 0 &&
            currentIndex <
                fields.length - 1
        ) {

            fields[
                currentIndex + 1
            ].focus();

        } else {

            current.blur();

        }

    }
);


// =============================
// START INCIDENT PAGE
// =============================

async function startIncidentsPage() {

    await loadCurrentUser();

    await loadIncidents();

}


startIncidentsPage();