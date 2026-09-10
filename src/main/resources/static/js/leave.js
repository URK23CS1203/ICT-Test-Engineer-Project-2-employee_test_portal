document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("leaveForm");

    if (form) {
        form.addEventListener("submit", (event) => {
            const startDate = document.getElementById("startDate")?.value ?? "";
            const endDate = document.getElementById("endDate")?.value ?? "";

            if (startDate && endDate && startDate > endDate) {
                event.preventDefault();
                window.alert("Start date cannot be after End date.");
            }
        });
    }

    window.leaveUtils = {
        isValidDateRange: (startDate, endDate) =>
            !startDate || !endDate || startDate <= endDate
    };
});
