/* ============================= */
/* THEME MANAGEMENT */
/* ============================= */

const savedTheme =
    localStorage.getItem("theme");

if (savedTheme === "dark") {
    document.body.classList.add("dark-theme");
}

function updateThemeButton() {

    const button =
        document.getElementById("themeToggle");

    if (!button) {
        return;
    }

    if (document.body.classList.contains("dark-theme")) {

        button.textContent = "☀️";
        button.title = "Switch to Light Mode";

    } else {

        button.textContent = "🌙";
        button.title = "Switch to Dark Mode";
    }
}

function toggleTheme() {

    document.body.classList.toggle(
        "dark-theme"
    );

    const isDark =
        document.body.classList.contains(
            "dark-theme"
        );

    localStorage.setItem(
        "theme",
        isDark ? "dark" : "light"
    );

    updateThemeButton();
}

document.addEventListener(
    "DOMContentLoaded",
    function() {

        updateThemeButton();

        const button =
            document.getElementById(
                "themeToggle"
            );

        if (button) {

            button.addEventListener(
                "click",
                toggleTheme
            );
        }
    }
);