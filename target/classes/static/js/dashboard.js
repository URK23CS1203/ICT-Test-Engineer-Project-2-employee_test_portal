document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("[data-dashboard-action]").forEach((element) => {
        element.addEventListener("click", () => {
            element.dataset.lastClicked = new Date().toISOString();
        });
    });

    window.portalUtils = {
        currentTimestamp: () => new Date().toISOString()
    };
});
