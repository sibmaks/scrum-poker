const sessionId = getSessionId();

function getSessionId() {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; X-Session-Id=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function logout() {
    $.ajax({
        method: "GET",
        url: "/api/user/logout",
        headers: {'X-Session-Id': getSessionId()},
        contentType: "application/json"
    })
    .done(function () {
        window.location.reload();
    });
}