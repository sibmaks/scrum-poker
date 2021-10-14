const loginInfoField = document.getElementById("login-info-field");
const passwordInfoField = document.getElementById("password-info-field");
const authErrorInfo = document.getElementById("auth-error-info");
const loginButton = document.getElementById("login-button");
const loginField = document.getElementById("login-form-login");
const passwordField = document.getElementById("login-form-password");

function doLogin() {
    const username = loginField.value;
    const password = passwordField.value;
    loginButton.disabled = true;
    const oldVal = loginButton.innerHTML;
    loginButton.innerHTML = '<i class="fas fa-spinner fa-pulse"></i>';
    loginInfoField.innerText = '';
    loginField.classList.remove('is-invalid');
    passwordInfoField.innerText = '';
    passwordField.classList.remove('is-invalid');

    $.ajax({
        method: "POST",
        url: "/api/user/login",
        contentType: "application/json",
        data: JSON.stringify({login: username, password: password})
    })
    .done(function (msg, textStatus, jqXHR ) {
        if(msg != null) {
            if(msg.resultCode === "Ok") {
                authErrorInfo.innerText = "";
                document.cookie = "X-Session-Id=" + jqXHR.getResponseHeader('X-Session-Id') + "; max-age=86400";
                window.location.reload();
            } else if(msg.resultCode === "NotFound") {
                authErrorInfo.innerText = "Login or password is incorrect";
            } else if(msg.resultCode === "ValidationError") {
                for(const validationError of msg.validationErrors) {
                    if("login" === validationError.field) {
                        loginInfoField.innerText = validationError.message;
                        loginField.classList.add('is-invalid');
                    } else if("password" === validationError.field) {
                        passwordInfoField.innerText = validationError.message;
                        passwordField.classList.add('is-invalid');
                    } else {
                        authErrorInfo.innerText = validationError.message;
                    }
                }
            } else {
                authErrorInfo.innerText = "Service error";
            }
        } else {
            authErrorInfo.innerText = "Service error";
        }
        loginButton.innerHTML = oldVal;
        loginButton.disabled = false;
    });
    return false;
}

function onKeyUp(e) {
    if (e.which === 10 || e.which === 13) {
        this.form.submit();
    }
}