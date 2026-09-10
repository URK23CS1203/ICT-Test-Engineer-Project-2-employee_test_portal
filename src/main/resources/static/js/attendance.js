document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("attendanceForm");

    if (form) {
        form.addEventListener("submit", () => {
            form.dataset.submittedAt = new Date().toISOString();
        });
    }

    window.loadAttendance = async () => {
        const response = await fetch("/api/attendance");

        if (!response.ok) {
            throw new Error(`Attendance API failed: ${response.status}`);
        }

        return response.json();
    };
});
