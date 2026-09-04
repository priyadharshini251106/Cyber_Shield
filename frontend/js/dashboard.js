const API_URL = "https://cybershield-xe22.onrender.com";

const sessionId =
    localStorage.getItem("sessionId");

const userRole =
    localStorage.getItem(
        "userRole"
    );

const userManagementLink =
    document.getElementById(
        "userManagementLink"
    );


if (
    userManagementLink &&
    (
        !userRole ||
        userRole.toUpperCase() !== "ADMIN"
    )
) {

    userManagementLink.style.display =
        "none";

}

if (!sessionId) {
    window.location.href = "../login.html";
}


// =============================
// GET HTML ELEMENTS
// =============================

const totalIncidents =
    document.getElementById("totalIncidents");

const openIncidents =
    document.getElementById("openIncidents");

const inProgressIncidents =
    document.getElementById("inProgressIncidents");

const resolvedIncidents =
    document.getElementById("resolvedIncidents");

const criticalIncidents =
    document.getElementById("criticalIncidents");

const welcomeText =
    document.getElementById("welcomeText");


// =============================
// SHOW LOGGED-IN EMAIL
// =============================

const email =
    localStorage.getItem("userEmail");

if (email) {
    welcomeText.textContent =
        "Logged in as: " + email;
}


// =============================
// LOAD DASHBOARD
// =============================

async function loadDashboard() {

    try {

        const response =
            await fetch(
                API_URL + "/api/dashboard",
                {
                    method: "GET",
                    headers: {
                        "Session-Id": sessionId
                    },
                    cache: "no-store"
                }
            );


        if (!response.ok) {

            throw new Error(
                "Dashboard API failed: " +
                response.status
            );
        }


        const data =
            await response.json();


        console.log(
            "Dashboard data:",
            data
        );


        // =============================
        // UPDATE STATISTICS
        // =============================

        totalIncidents.textContent =
            data.total;

        openIncidents.textContent =
            data.open;

        inProgressIncidents.textContent =
            data.inProgress;

        resolvedIncidents.textContent =
            data.resolved;

        criticalIncidents.textContent =
            data.critical;
        // =============================
        // DISPLAY CHARTS
        // =============================

        displayStatusChart(
            data
        );

        displaySeverityChart(
            data
        );

    } catch (error) {

        console.error(
            "Dashboard error:",
            error
        );

    }
}


// =============================
// LOGOUT
// =============================

const logoutButton =
    document.getElementById("logoutButton");

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
                    API_URL + "/api/logout",
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


            window.location.href =
                "../login.html";
        }
    );
}


// =============================
// START DASHBOARD
// =============================

loadDashboard();

// =============================
// CREATE BAR
// =============================

function createBar(
    label,
    value,
    maximum
) {

    const row =
        document.createElement(
            "div"
        );

    row.className =
        "chart-row";


    const labelElement =
        document.createElement(
            "span"
        );

    labelElement.className =
        "chart-label";

    labelElement.textContent =
        label;


    const barContainer =
        document.createElement(
            "div"
        );

    barContainer.className =
        "bar-container";


    const bar =
        document.createElement(
            "div"
        );

    bar.className =
        "bar";


    let percentage = 0;


    if (maximum > 0) {

        percentage =
            (value / maximum) * 100;

    }


    bar.style.width =
        percentage + "%";


    const valueElement =
        document.createElement(
            "span"
        );

    valueElement.className =
        "chart-value";

    valueElement.textContent =
        value;


    barContainer.appendChild(
        bar
    );


    row.appendChild(
        labelElement
    );


    row.appendChild(
        barContainer
    );


    row.appendChild(
        valueElement
    );


    return row;
}


// =============================
// STATUS CHART
// =============================

function displayStatusChart(
    data
) {

    const chart =
        document.getElementById(
            "statusChart"
        );


    if (!chart) {

        return;

    }


    chart.innerHTML = "";


    const maximum =
        Math.max(
            data.open,
            data.inProgress,
            data.resolved
        );


    chart.appendChild(
        createBar(
            "Open",
            data.open,
            maximum
        )
    );


    chart.appendChild(
        createBar(
            "In Progress",
            data.inProgress,
            maximum
        )
    );


    chart.appendChild(
        createBar(
            "Resolved",
            data.resolved,
            maximum
        )
    );

}


// =============================
// SEVERITY CHART
// =============================

function displaySeverityChart(
    data
) {

    const chart =
        document.getElementById(
            "severityChart"
        );


    if (!chart) {

        return;

    }


    chart.innerHTML = "";


    /*
     * The current dashboard API
     * provides the critical count.
     *
     * The other severity counts
     * are calculated from incidents.
     */

    loadSeverityChart();

}


// =============================
// LOAD SEVERITY DATA
// =============================

async function loadSeverityChart() {

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

            return;

        }


        const incidents =
            await response.json();


        let low = 0;

        let medium = 0;

        let high = 0;

        let critical = 0;


        incidents.forEach(
            function(incident) {

                const severity =
                    incident.severity
                        .toUpperCase();


                if (
                    severity === "LOW"
                ) {

                    low++;

                }


                else if (
                    severity === "MEDIUM"
                ) {

                    medium++;

                }


                else if (
                    severity === "HIGH"
                ) {

                    high++;

                }


                else if (
                    severity === "CRITICAL"
                ) {

                    critical++;

                }

            }
        );


        const maximum =
            Math.max(
                low,
                medium,
                high,
                critical
            );


        const chart =
            document.getElementById(
                "severityChart"
            );


        chart.innerHTML = "";


        chart.appendChild(
            createBar(
                "Low",
                low,
                maximum
            )
        );


        chart.appendChild(
            createBar(
                "Medium",
                medium,
                maximum
            )
        );


        chart.appendChild(
            createBar(
                "High",
                high,
                maximum
            )
        );


        chart.appendChild(
            createBar(
                "Critical",
                critical,
                maximum
            )
        );


    } catch (error) {

        console.error(
            "Unable to load severity data.",
            error
        );

    }

}