const loginForm =
    document.getElementById("loginForm");
const registeredEmail =
    localStorage.getItem(
        "registeredEmail"
    );


if (registeredEmail) {

    document.getElementById(
        "email"
    ).value =
        registeredEmail;


    localStorage.removeItem(
        "registeredEmail"
    );


    document.getElementById(
        "password"
    ).focus();
}

loginForm.addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();


        const email =
            document.getElementById("email")
                .value
                .trim();


        const password =
            document.getElementById("password")
                .value
                .trim();


        const message =
            document.getElementById("message");


        if (
            email === "" ||
            password === ""
        ) {

            message.textContent =
                "Please fill in all fields.";

            return;
        }


        message.textContent =
            "Logging in...";


        try {

            const response =
                await fetch(
                    "https://cybershield-xe22.onrender.com/api/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            email: email,
                            password: password
                        })
                    }
                );


            const data =
                await response.json();


            if (data.success === true) {

                localStorage.setItem(
                    "sessionId",
                    data.sessionId
                );
                localStorage.setItem(
                    "userRole",
                    data.role
                );

                localStorage.setItem(
                    "userEmail",
                    email
                );


                window.location.href =
                    "pages/dashboard.html";

            } else {

                message.textContent =
                    data.message;
            }


        } catch (error) {

            console.error(error);

            message.textContent =
                "Cannot connect to Java server.";
        }

    }
);

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