angular.module('pokerApp', [])
    .controller('RegistrationController', ['$scope', function ObjectArrayCtrl($scope) {
        $scope.login = '';
        $scope.password = '';
        $scope.firstName = '';
        $scope.lastName = '';
        $scope.loginInfoField = '';
        $scope.passwordInfoField = '';
        $scope.firstNameInfoField = '';
        $scope.lastNameInfoField = '';
        $scope.regErrorInfo = '';

        $scope.doRegistration = function () {
            $('#reg-btn').each(function (item) {
                item.disabled = true;
            })
            $scope.regErrorInfo = '';
            $scope.loginInfoField = '';
            $scope.passwordInfoField = '';
            $scope.firstNameInfoField = '';
            $scope.lastNameInfoField = '';
            $.ajax({
                method: "POST",
                url: "/api/user/registration",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({login: $scope.login, password: $scope.password, firstName: $scope.firstName,
                    lastName: $scope.lastName})
            })
            .done(function (msg, textStatus, jqXHR) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        document.cookie = "X-Session-Id=" + jqXHR.getResponseHeader('X-Session-Id') + "; max-age=86400";
                        window.location.replace('/');
                    } else if(msg.resultCode === "ValidationError") {
                        $scope.$apply(function () {
                            for (const validationError of msg.validationErrors) {
                                if ("login" === validationError.field) {
                                    $scope.loginInfoField = validationError.message;
                                } else if ("password" === validationError.field) {
                                    $scope.passwordInfoField = validationError.message;
                                } else if ("firstName" === validationError.field) {
                                    $scope.firstNameInfoField = validationError.message;
                                } else if ("lastName" === validationError.field) {
                                    $scope.lastNameInfoField = validationError.message;
                                } else {
                                    $scope.regErrorInfo = validationError.message;
                                }
                            }
                        });
                    } else if(msg.resultCode === "LoginIsBusy") {
                        $scope.$apply(function () {
                            $scope.loginInfoField = "User with the same login already exists";
                        });
                    } else {
                        $scope.$apply(function () {
                            $scope.regErrorInfo = "Service error";
                        });
                    }
                } else {
                    $scope.$apply(function () {
                        $scope.regErrorInfo = "Service error";
                    });
                }
                $('#reg-btn').each(function (item) {
                    item.disabled = true;
                })
            });
        }
    }]);