$(document).ready(() => {
    $("#registerButton").click(registerCar);
});

function registerCar() {
    let input = $("#carData");
    let text = input.val();
    if (text.length === 0) {
        return;
    }
    input.val("");

    $.ajax({
        type: "POST",
        contentType: "application/json",
        url: "PostService",
        data: JSON.stringify({ "registration": text }),
        success: showCar
    });
}

function showCar(data) {
    $("#carList").find("tbody:last-child").append("<tr><td>" + data.id + "</td><td>" + data.registration + "</td></tr>");
}