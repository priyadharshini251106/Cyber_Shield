const API_BASE_URL = "http://localhost:8080";

const sessionId = localStorage.getItem("sessionId");

const userId = document.getElementById("userId");
const userName = document.getElementById("userName");
const userEmail = document.getElementById("userEmail");
const userRole = document.getElementById("userRole");
const userStatus = document.getElementById("userStatus");
const message = document.getElementById("message");
const logoutButton = document.getElementById("logoutButton");


async function loadProfile() {

    if (!sessionId) {

        window.location.href = "../login.html";
        return;
    }

    try {

        const response = await fetch(`${API_BASE_URL}/api/profile`, {

            method: "GET",

            headers: {
                "Session-Id": sessionId
            }

        });


        if (!response.ok) {

            if (response.status === 401 || response.status === 403) {

                localStorage.removeItem("sessionId");
                localStorage.removeItem("userRole");

                window.location.href = "../login.html";

                return;
            }

            throw new Error("Failed to load profile");
        }


        const data = await response.json();


        userId.textContent = data.id ?? "-";
        userName.textContent = data.name ?? "-";
        userEmail.textContent = data.email ?? "-";
        userRole.textContent = data.role ?? "-";


        if (data.status === true) {

            userStatus.textContent = "Active";

        } else {

            userStatus.textContent = "Inactive";
        }


        // Keep localStorage role synchronized
        if (data.role) {

            localStorage.setItem("userRole", data.role);
        }


        message.textContent = "";

    } catch (error) {

        console.error("Profile error:", error);

        message.textContent =
            "Unable to load profile. Please try again.";

    }
}


logoutButton.addEventListener("click", async function (event) {

    event.preventDefault();

    try {

        await fetch(`${API_BASE_URL}/api/logout`, {

            method: "POST",

            headers: {
                "Session-Id": sessionId
            }

        });

    } catch (error) {

        console.error("Logout error:", error);

    }


    localStorage.removeItem("sessionId");
    localStorage.removeItem("userRole");
    localStorage.removeItem("registeredEmail");

    window.location.href = "../login.html";
});


loadProfile();