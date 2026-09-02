const API_URL =
    "http://localhost:8080";


// ==========================================
// SESSION
// ==========================================

const sessionId =
    localStorage.getItem(
        "sessionId"
    );

const userRole =
    localStorage.getItem(
        "userRole"
    );


// ==========================================
// SESSION CHECK
// ==========================================

if (!sessionId) {

    window.location.href =
        "../login.html";

}


// ==========================================
// ADMIN CHECK
// ==========================================

if (
    !userRole ||
    userRole.toUpperCase() !== "ADMIN"
) {

    alert(
        "Access denied. Admin only."
    );

    window.location.href =
        "dashboard.html";

}


// ==========================================
// HTML ELEMENTS
// ==========================================

const tableBody =
    document.getElementById(
        "userTableBody"
    );


const message =
    document.getElementById(
        "message"
    );


// ==========================================
// LOAD USERS
// ==========================================

async function loadUsers() {

    try {

        const response =
            await fetch(
                API_URL +
                "/api/users",
                {
                    method: "GET",

                    headers: {
                        "Session-Id":
                            sessionId
                    }
                }
            );


        if (!response.ok) {

            if (
                response.status === 403
            ) {

                alert(
                    "Access denied. Admin only."
                );

                window.location.href =
                    "dashboard.html";

                return;

            }


            throw new Error(
                "Unable to load users"
            );

        }


        const users =
            await response.json();


        displayUsers(users);


    } catch (error) {

        console.error(
            "User loading error:",
            error
        );


        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    Unable to load users.
                </td>
            </tr>
        `;

    }

}


// ==========================================
// DISPLAY USERS
// ==========================================

function displayUsers(users) {

    tableBody.innerHTML = "";


    if (users.length === 0) {

        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    No users found.
                </td>
            </tr>
        `;

        return;

    }


    users.forEach(
        function(user) {

            const row =
                document.createElement(
                    "tr"
                );


            // ==================================
            // STATUS
            // ==================================

            const statusText =
                user.status
                    ? "Active"
                    : "Disabled";


            const statusClass =
                user.status
                    ? "status-active"
                    : "status-disabled";


            // ==================================
            // DEFAULT ADMIN
            // ==================================

            const isDefaultAdmin =
                user.email.toLowerCase() ===
                "admin@cybershield.com";


            let actionHTML =
                "";


            if (isDefaultAdmin) {

                actionHTML = `
                    <span class="protected-user">
                        Protected Admin
                    </span>
                `;

            } else {

                const newRole =
                    user.role.toUpperCase() ===
                    "ADMIN"
                        ? "USER"
                        : "ADMIN";


                const roleButtonText =
                    user.role.toUpperCase() ===
                    "ADMIN"
                        ? "Make User"
                        : "Make Admin";


                const statusButtonText =
                    user.status
                        ? "Disable"
                        : "Enable";


                actionHTML = `

                    <button
                        type="button"
                        class="user-action-button"
                        onclick="changeRole(
                            ${user.id},
                            '${newRole}'
                        )"
                    >
                        ${roleButtonText}
                    </button>


                    <button
                        type="button"
                        class="user-status-button"
                        onclick="changeStatus(
                            ${user.id},
                            ${!user.status}
                        )"
                    >
                        ${statusButtonText}
                    </button>

                `;

            }


            // ==================================
            // ROW
            // ==================================

            row.innerHTML = `

                <td>
                    ${user.id}
                </td>

                <td>
                    ${escapeHtml(
                        user.name
                    )}
                </td>

                <td>
                    ${escapeHtml(
                        user.email
                    )}
                </td>

                <td>

                    <span
                        class="role-badge
                        ${user.role.toLowerCase()}"
                    >
                        ${user.role}
                    </span>

                </td>

                <td>

                    <span
                        class="${statusClass}"
                    >
                        ${statusText}
                    </span>

                </td>

                <td>

                    <div class="user-actions">

                        ${actionHTML}

                    </div>

                </td>

            `;


            tableBody.appendChild(
                row
            );

        }
    );

}


// ==========================================
// CHANGE ROLE
// ==========================================

async function changeRole(
    userId,
    newRole
) {

    const confirmed =
        confirm(
            "Change this user's role to "
            + newRole
            + "?"
        );


    if (!confirmed) {

        return;

    }


    try {

        const response =
            await fetch(
                API_URL +
                "/api/users/" +
                userId +
                "/role",
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Session-Id":
                            sessionId
                    },

                    body:
                        JSON.stringify({
                            role: newRole
                        })
                }
            );


        const data =
            await response.json();


        if (!response.ok) {

            alert(
                data.message ||
                "Unable to change role."
            );

            return;

        }


        message.textContent =
            data.message;


        await loadUsers();


    } catch (error) {

        console.error(
            "Role update error:",
            error
        );


        alert(
            "Unable to change user role."
        );

    }

}


// ==========================================
// CHANGE STATUS
// ==========================================

async function changeStatus(
    userId,
    newStatus
) {

    const action =
        newStatus
            ? "enable"
            : "disable";


    const confirmed =
        confirm(
            "Are you sure you want to "
            + action
            + " this user?"
        );


    if (!confirmed) {

        return;

    }


    try {

        const response =
            await fetch(
                API_URL +
                "/api/users/" +
                userId +
                "/status",
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Session-Id":
                            sessionId
                    },

                    body:
                        JSON.stringify({
                            status: newStatus
                        })
                }
            );


        const data =
            await response.json();


        if (!response.ok) {

            alert(
                data.message ||
                "Unable to change status."
            );

            return;

        }


        message.textContent =
            data.message;


        await loadUsers();


    } catch (error) {

        console.error(
            "Status update error:",
            error
        );


        alert(
            "Unable to change user status."
        );

    }

}


// ==========================================
// ESCAPE HTML
// ==========================================

function escapeHtml(
    value
) {

    if (value === null ||
        value === undefined) {

        return "";

    }


    return String(value)
        .replace(
            /&/g,
            "&amp;"
        )
        .replace(
            /</g,
            "&lt;"
        )
        .replace(
            />/g,
            "&gt;"
        )
        .replace(
            /"/g,
            "&quot;"
        )
        .replace(
            /'/g,
            "&#039;"
        );

}


// ==========================================
// LOGOUT
// ==========================================

const logoutButton =
    document.getElementById(
        "logoutButton"
    );


if (logoutButton) {

    logoutButton.addEventListener(
        "click",
        async function(event) {

            event.preventDefault();


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


// ==========================================
// START
// ==========================================

loadUsers();