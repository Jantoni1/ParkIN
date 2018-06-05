$(document).ready(() => {
    $("#reset").click(sendReset);
});

function sendReset() {
    $.get("reset-all", response => {
        showAlert(true, messages.RESET_SUCCESS);
    });
}