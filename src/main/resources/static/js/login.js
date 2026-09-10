document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    const username = document.getElementById("username");
    const password = document.getElementById("password");
    const message = document.getElementById("loginClientError");

    if (!form || !username || !password) return;

    form.addEventListener("submit", (event) => {
        const userValue = username.value.trim();
        const passwordValue = password.value.trim();

        if (!userValue || !passwordValue) {
            event.preventDefault();

            if (message) {
                message.textContent = "Username and password are required.";
                message.style.display = "block";
            }
        } else if (message) {
            message.textContent = "";
            message.style.display = "none";
        }
    });
});
