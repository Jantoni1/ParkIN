let chartHandler;

$(document).ready(() => {
    prepareChart();
    updateOccupancy();

    $("#registerButton").click(registerCar);
    $("#registerInput").keyup((event) => {
        if (event.keyCode === 13) {
            registerCar();
        }
    });

    $("#checkoutButton").click(checkOut);
    $("#checkoutInput").keyup((event) => {
        if (event.keyCode === 13) {
            checkOut();
        }
    });

    $("#cancelReleaseButton").click(hideTicket);
    $("#releaseButton").click(release);
});

function prepareChart() {
    chartHandler = new Chart($("#chart"), {
        type: 'pie',
        data: {
            datasets:[{
                data: [0, 0],
                backgroundColor: ["#e95a0c", "#00a0a8"]
            }],
            labels: ["Occupied", "Free"]
        },
        options: {
            legend: {
                position: "bottom",
            }
        }
    });
}

function updateOccupancy() {
    $.get("slots-info", response => {
        $("#capacity").text(response.capacity);
        $("#occupied").text(response.occupied);
        $("#free").text(response.capacity - response.occupied);
        $("#full").toggleClass("invisible", response.occupied < response.capacity);

        chartHandler.data.datasets[0].data = [response.occupied, response.capacity - response.occupied];
        chartHandler.update();
    });
}

function registerCar() {
    let text = getInput($("#registerInput"), true);
    if (text.length === 0) {
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "register",
        data: JSON.stringify({ registrationPlate: text }),
        success: () => {
            showAlert(true, messages.REGISTER_SUCCESS);
            updateOccupancy();
        },
        error: jqXHR => {
            switch (jqXHR.status) {
                case 400:
                    showAlert(false, messages.REGISTER_FAIL_CONFLICT);
                    break;
                case 403:
                    showAlert(false, messages.REGISTER_FAIL_FULL);
                    break;
                case 406:
                    showAlert(false, messages.REGISTER_FAIL_TOO_LONG);
                    break;
            }
        }
    });
}

function checkOut() {
    let text = getInput($("#checkoutInput"), true);
    if (text.length === 0) {
        return;
    }

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "checkout",
        data: JSON.stringify({ registrationPlate: text }),
        success: response => {
            showTicket(response);
        },
        error: () => {
            showAlert(false, messages.CHECKOUT_FAIL);
        }
    });
}

function showTicket(data) {
    $("#checkout").fadeOut(250, () => {
        $("#release").fadeIn(250);
    });

    $("#checkoutRegistration").text(data.registrationPlate);
    $("#checkoutArrival").text(moment(data.arrival).format("YYYY-MM-DD HH:mm:ss"));
    $("#checkoutDeparture").text(moment(data.departure).format("YYYY-MM-DD HH:mm:ss"));
    $("#checkoutFee").text(data.fee);
}

function hideTicket() {
    $("#release").fadeOut(250, () => {
        $("#checkout").fadeIn(250);
    });
}

function release() {
    hideTicket();

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "unregister",
        data: JSON.stringify({
            registrationPlate: $("#checkoutRegistration").text(),
            departure: $("#checkoutDeparture").text()
        }),
        success: () => {
            showAlert(true, messages.RELEASE_SUCCESS);
            updateOccupancy();
        }
    });
}
