const messages = {
    TARIFF_SUCCESS: "Tariff successfully updated",
    REGISTER_SUCCESS: "Car registration successful",
    REGISTER_FAIL_TOO_LONG: "Registration plate cannot exceed 12 characters",
    REGISTER_FAIL_CONFLICT: "Car is already registered",
    REGISTER_FAIL_FULL: "Parking lot is already full",
    CHECKOUT_FAIL: "Invalid registration plate",
    RELEASE_SUCCESS: "Payment registered",
    RESET_SUCCESS: "All car entries removed"
};

function getInput(inputElement, clear = false) {
    let text = inputElement.val();
    if (clear) {
        inputElement.val("");
    }
    return text;
}

function showAlert(success, text) {
    $(document).scrollTop(0);
    let alertElement = success ? $(".alert-success") : $(".alert-danger");
    alertElement.find(".alertText").text(text);
    alertElement.fadeIn(500).delay(1000).fadeOut(500);
}