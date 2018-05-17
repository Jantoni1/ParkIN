$(document).ready(getStatistics);

function getStatistics() {
    $.get("statistics", response => {
        $("#dailyEarnings").text(response.earnings); //TODO uzgodnic nazwy
        $("#arrivals").text(response.entered);
        $("#departures").text(response.left);
    });
}