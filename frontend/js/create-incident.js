const API_URL =
    "http://localhost:8080";


const incidentForm =
    document.getElementById(
        "incidentForm"
    );


const message =
    document.getElementById(
        "message"
    );


const formTitle =
    document.getElementById(
        "formTitle"
    );


const submitButton =
    document.getElementById(
        "submitButton"
    );


const statusGroup =
    document.getElementById(
        "statusGroup"
    );


let editMode = false;
let incidentId = null;


// =============================
// CHECK LOGIN SESSION
// =============================

const sessionId =
    localStorage.getItem(
        "sessionId"
    );


if (!sessionId) {

    window.location.href =
        "../login.html";

}


// =============================
// CHECK URL
// =============================

const params =
    new URLSearchParams(
        window.location.search
    );


if (params.has("id")) {

    editMode = true;

    incidentId =
        params.get("id");

    formTitle.textContent =
        "Edit Incident";

    submitButton.textContent =
        "Update Incident";

    statusGroup.style.display =
        "block";

    loadIncident(
        incidentId
    );
}


// =============================
// LOAD ONE INCIDENT
// =============================

async function loadIncident(id) {

    try {

        const response =
            await fetch(
                API_URL +
                "/api/incidents/" +
                id
            );


        if (!response.ok) {

            message.textContent =
                "Incident not found.";

            return;
        }


        const incident =
            await response.json();


        document.getElementById(
            "title"
        ).value =
            incident.title;


        document.getElementById(
            "description"
        ).value =
            incident.description;


        document.getElementById(
            "category"
        ).value =
            incident.category;


        document.getElementById(
            "severity"
        ).value =
            incident.severity;


        document.getElementById(
            "status"
        ).value =
            incident.status;


    } catch (error) {

        console.error(error);

        message.textContent =
            "Unable to load incident.";

    }
}


// =============================
// SUBMIT
// =============================

incidentForm.addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();


        const title =
            document.getElementById(
                "title"
            ).value.trim();


        const description =
            document.getElementById(
                "description"
            ).value.trim();


        const category =
            document.getElementById(
                "category"
            ).value;


        const severity =
            document.getElementById(
                "severity"
            ).value;


        if (
            title === "" ||
            description === "" ||
            category === "" ||
            severity === ""
        ) {

            message.textContent =
                "Please fill in all fields.";

            return;
        }


        // =============================
        // CREATE
        // =============================

        if (!editMode) {

            await createIncident(
                title,
                description,
                category,
                severity
            );

            return;
        }


        // =============================
        // UPDATE
        // =============================

        const status =
            document.getElementById(
                "status"
            ).value;


        await updateIncident(
            title,
            description,
            category,
            severity,
            status
        );

    }
);


// =============================
// GET LOGGED-IN USER ID
// =============================

async function getLoggedInUserId() {

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

            return null;

        }


        const user =
            await response.json();


        return user.id;


    } catch (error) {

        console.error(error);

        return null;

    }
}


// =============================
// CREATE INCIDENT
// =============================

async function createIncident(
    title,
    description,
    category,
    severity
) {

    // Get the actual logged-in
    // user's ID

    const reportedBy =
        await getLoggedInUserId();


    if (!reportedBy) {

        message.textContent =
            "Unable to identify logged-in user.";

        return;
    }


    try {

        const response =
            await fetch(
                API_URL +
                "/api/incidents",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Session-Id":
                            sessionId
                    },

                    body: JSON.stringify({

                        title:
                            title,

                        description:
                            description,

                        category:
                            category,

                        severity:
                            severity,

                        reportedBy:
                            reportedBy

                    })
                }
            );


        const data =
            await response.json();


        message.textContent =
            data.message;


        if (response.ok) {

            incidentForm.reset();


            setTimeout(
                function() {

                    window.location.href =
                        "incidents.html";

                },
                1000
            );

        }


    } catch (error) {

        console.error(error);

        message.textContent =
            "Cannot connect to Java server.";

    }
}


// =============================
// UPDATE INCIDENT
// =============================

async function updateIncident(
    title,
    description,
    category,
    severity,
    status
) {

    try {

        const response =
            await fetch(
                API_URL +
                "/api/incidents/" +
                incidentId,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Session-Id":
                            sessionId
                    },

                    body: JSON.stringify({

                        title:
                            title,

                        description:
                            description,

                        category:
                            category,

                        severity:
                            severity,

                        status:
                            status

                    })
                }
            );


        const data =
            await response.json();


        message.textContent =
            data.message;


        if (response.ok) {

            setTimeout(
                function() {

                    window.location.href =
                        "incidents.html";

                },
                1000
            );

        }


    } catch (error) {

        console.error(error);

        message.textContent =
            "Cannot connect to Java server.";

    }
}


// =============================
// LOGOUT
// =============================

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


logoutButton.addEventListener(
    "click",
    async function(event) {

        event.preventDefault();


        const sessionId =
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
                            sessionId
                    }
                }
            );

        } catch (error) {

            console.error(error);

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