$(document).ready(() => {
    getTariff();
    $(".needs-validation").on("submit", event => {
        $(event.currentTarget).addClass("was-validated");
        event.preventDefault();
        if (event.currentTarget.checkValidity()) {
            $(event.currentTarget).removeClass("was-validated");
            postTariff();
            setClean();
        }
    });
    $(":input").bind('keyup mouseup', () => {
        if (isDirty()) {
            $("button").prop("disabled", false).removeClass("disabled");
        }
        else {
            $("button").prop("disabled", true).addClass("disabled");
        }
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

function setClean() {
    $("button").prop("disabled", true).addClass("disabled");
    $(":input").each(function() {
        $(this).data("initialValue", $(this).val());
    });
}

function isDirty() {
    let dirty = false;
    $(":input").each(function() {
        if ($(this).data("initialValue") !== $(this).val()) {
            dirty = true;
        }
    });
    return dirty;
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
            showAlert(true, messages.TARIFF_SUCCESS);
        }
    });
}