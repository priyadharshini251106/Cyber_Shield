const registerForm =
    document.getElementById("registerForm");

registerForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const name =
        document.getElementById("name").value.trim();

    const email =
        document.getElementById("email").value.trim();

    const password =
        document.getElementById("password").value.trim();

    const confirmPassword =
        document.getElementById("confirmPassword").value.trim();

    const message =
        document.getElementById("message");


    // Frontend validation

    if (name === "" ||
        email === "" ||
        password === "" ||
        confirmPassword === "") {

        message.textContent =
            "Please fill in all fields.";

        return;
    }


    if (password.length < 6) {

        message.textContent =
            "Password must contain at least 6 characters.";

        return;
    }


    if (password !== confirmPassword) {

        message.textContent =
            "Passwords do not match.";

        return;
    }


    // Show processing message

    message.textContent =
        "Registering...";


    try {

        const response = await fetch(
            "http://localhost:8080/api/register",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: password
                })
            }
        );


        const data =
            await response.json();


        console.log("Server response:", data);


        message.textContent =
            data.message;


        if (response.ok) {

    message.textContent =
        data.message;


    // Save the registered email

    localStorage.setItem(
        "registeredEmail",
        email
    );


    // Redirect to login

    setTimeout(
        function() {

            window.location.href =
                "login.html";

        },
        1000
    );
}


    } catch (error) {

        console.error(
            "Registration error:",
            error
        );

        message.textContent =
            "Cannot connect to Java server.";
    }

});

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
            currentIndex < fields.length - 1
        ) {

            fields[currentIndex + 1].focus();

        } else {

            current.blur();
        }
    }
);