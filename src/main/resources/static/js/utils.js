const messages = {
    TARIFF_SUCCESS: "Tariff successfully updated",
    REGISTER_SUCCESS: "Car registration successful",
    REGISTER_FAIL: "Car registration failed",
    CHECKOUT_FAIL: "Invalid registration plate",
    RELEASE_SUCCESS: "Payment registered"
};

function getInput(inputElement, clear = false) {
    let text = inputElement.val();
    if (clear) {
        inputElement.val("");
    }
    return text;
}

function showAlert(success, text) {
    let alertElement = success ? $(".alert-success") : $(".alert-danger");
    alertElement.find(".alertText").text(text);
    alertElement.fadeIn(500).delay(1000).fadeOut(500);
}