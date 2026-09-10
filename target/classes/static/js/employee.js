document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("employeeForm");
    const email = document.getElementById("employeeEmail");

    if (form) {
        form.addEventListener("submit", (event) => {
            const value = email?.value.trim() ?? "";
            if (value && !value.includes("@")) {
                event.preventDefault();
                window.alert("Enter a valid email address.");
                email?.focus();
            }
        });
    }

    document.querySelectorAll(".danger").forEach((button) => {
        button.addEventListener("click", (event) => {
            if (!window.confirm("Deactivate this employee and disable the linked login account?")) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll(".success-btn").forEach((button) => {
        button.addEventListener("click", (event) => {
            if (!window.confirm("Activate this employee and enable the linked login account?")) {
                event.preventDefault();
            }
        });
    });

    window.loadEmployees = async () => {
        const response = await fetch("/api/employees");
        if (!response.ok) throw new Error(`Employee API failed: ${response.status}`);
        return response.json();
    };
});
