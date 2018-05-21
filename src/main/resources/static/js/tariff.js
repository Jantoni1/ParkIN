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
    $(":input").bind("keyup mouseup", () => {
        let dirty = false;
        $(":input").each(function() {
            if ($(this).data("initialValue") !== $(this).val()) {
                dirty = true;
            }
        });
        $("#submit").prop("disabled", !dirty).toggleClass("disabled", !dirty);
    });
});

function getTariff() {
    $.get("tariff-data", response => {
        $("#basicFee").val(response.basicBid);
        $("#basicPeriod").val(response.basicPeriod);
        $("#extendedFee").val(response.extendedBid);
        setClean();
    });
}

function postTariff() {
    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "tariff-data",
        data: JSON.stringify({
            basicBid: $("#basicFee").val(),
            basicPeriod: $("#basicPeriod").val(),
            extendedBid: $("#extendedFee").val()
        }),
        success: () => {
            setClean();
            showAlert(true, messages.TARIFF_SUCCESS);
        }
    });
}

function setClean() {
    $("#submit").prop("disabled", true).addClass("disabled");
    $(":input").each(function() {
        $(this).data("initialValue", $(this).val());
    });
}