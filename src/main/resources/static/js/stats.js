let chartHandler;

$(document).ready(() => {
    getStatistics();
});

function getStatistics() {
    $.get("stats-day", response => {
        $("#dailyEarnings").text(response.earnings);
        $("#arrivals").text(response.arrivals);
        $("#departures").text(response.departures);
        $("#forecast").text(response.forecast);
    });

    $.get("stats-month", response => {
        chartHandler = new Chart($("#chart"), {
            type: 'bar',
            data: {
                datasets:[{
                    data: response.map((elem) => elem.earnings),
                    backgroundColor: "rgba(54, 162, 235, 0.6)"
                }],
                labels: response.map((elem) => elem.date)
            },
            options: {
                maintainAspectRatio: false,
                legend: {
                    display: false,
                },
                scales: {
                    yAxes: [{
                        ticks: {
                            callback: (value, index, values) => {
                                return value + " \u20AC";
                            },
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    });
}