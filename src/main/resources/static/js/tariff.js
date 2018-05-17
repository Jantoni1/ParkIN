$(document).ready(() => {
    getTariff();
    $(".needs-validation").on("submit", event => {
        $(event.currentTarget).addClass("was-validated");
        event.preventDefault();
        if (event.currentTarget.checkValidity()) {
            $(event.currentTarget).removeClass("was-validated");
            postTariff();
        }
    });
});

function getTariff() {
    $.get("tariff-data", response => {
        $("#basicFee").val(response.basicBid);
        $("#basicPeriod").val(response.basicPeriod);
        $("#extendedFee").val(response.extendedBid);
    });
}

function postTariff() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "tariff-data",
        data: JSON.stringify({
            basicBid: getInput($("#basicFee")),
            basicPeriod: getInput($("#basicPeriod")),
            extendedBid: getInput($("#extendedFee"))
        }),
        success: () => {
            showAlert(true, messages.TARIFF_SUCCESS);
        }
    });
}
