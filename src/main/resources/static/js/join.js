function doJoin() {
    const roomSecretCodeElement = document.getElementById("room-secret-code")
    const roomSecretCode = roomSecretCodeElement === null || roomSecretCodeElement === undefined ? null : roomSecretCodeElement.value;
    const joinErrorInfo = document.getElementById("join-error-info");
    const roleId = document.getElementById("role-select").value;
    const joinButton = document.getElementById("join-btn");
    joinButton.disabled = true;
    const oldVal = joinButton.innerHTML;
    joinButton.innerHTML = '<i class="fas fa-spinner fa-pulse"></i>';

    $.ajax({
        method: "POST",
        url: "/api/room/join",
        headers: {'X-Session-Id': sessionId},
        contentType: "application/json",
        data: JSON.stringify({roleId: roleId, roomId: roomId, secretCode: roomSecretCode})
    })
    .done(function (msg) {
        if(msg != null) {
            if(msg.resultCode === "Ok") {
                joinErrorInfo.innerText = "";
                window.location.reload();
            } else if(msg.resultCode === "Unauthorized") {
                window.location.reload();
            }  else if(msg.resultCode === "WrongSecretCode") {
                joinErrorInfo.innerText = "Wrong secret code entered";
            } else {
                joinErrorInfo.innerText = "Service error";
            }
        } else {
            joinErrorInfo.innerText = "Service error";
        }
        joinButton.innerHTML = oldVal;
        joinButton.disabled = false;
    });
}