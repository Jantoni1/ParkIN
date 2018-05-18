$(document).ready(getStatistics);

function getStatistics() {
    $.get("statistics", response => {
        $("#dailyEarnings").text(response.earnings);
        $("#arrivals").text(response.arrivals);
        $("#departures").text(response.departures);
    });
}