angular.module('pokerApp', [])
    .controller('AccountController', ['$scope', function ObjectArrayCtrl($scope) {
        $scope.password = '';
        $scope.firstName = firstName;
        $scope.lastName = lastName;
        $scope.accSuccessInfo = '';
        $scope.passwordInfoField = '';
        $scope.firstNameInfoField = '';
        $scope.lastNameInfoField = '';
        $scope.accErrorInfo = '';

        $scope.saveAccountInfo = function () {
            $scope.accSuccessInfo = '';
            $scope.accErrorInfo = '';
            $('#save-btn').each(function (item) {
                item.disabled = true;
            })
            $.ajax({
                method: "POST",
                url: "/api/user/update",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({firstName: $scope.firstName, lastName: $scope.lastName})
            })
            .done(function (msg) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        $scope.$apply(function () {
                            $scope.accSuccessInfo = 'Saved!';
                        });
                    } else if (msg.resultCode === "ValidationError") {
                        $scope.$apply(function () {
                            for (const validationError of msg.validationErrors) {
                                if ("firstName" === validationError.field) {
                                    $scope.firstNameInfoField = validationError.message;
                                } else if ("lastName" === validationError.field) {
                                    $scope.lastNameInfoField = validationError.message;
                                } else {
                                    $scope.regErrorInfo = validationError.message;
                                }
                            }
                        });
                    } else {
                        $scope.$apply(function () {
                            $scope.accErrorInfo = 'Service error';
                        });
                    }
                } else {
                    $scope.$apply(function () {
                        $scope.accErrorInfo = 'Service error';
                    });
                }
                $('#save-btn').each(function (item) {
                    item.disabled = true;
                })
            });
        }

        $scope.savePassword = function () {
            $scope.accSuccessInfo = '';
            $scope.accErrorInfo = '';
            $('#save-password-btn').each(function (item) {
                item.disabled = true;
            })
            $.ajax({
                method: "POST",
                url: "/api/user/changePassword",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({password: $scope.password})
            })
            .done(function (msg) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        $scope.$apply(function () {
                            $scope.accSuccessInfo = 'Saved!';
                        });
                    } else if (msg.resultCode === "ValidationError") {
                        $scope.$apply(function () {
                            for (const validationError of msg.validationErrors) {
                                if ("password" === validationError.field) {
                                    $scope.passwordInfoField = validationError.message;
                                } else {
                                    $scope.regErrorInfo = validationError.message;
                                }
                            }
                        });
                    } else {
                        $scope.$apply(function () {
                            $scope.accErrorInfo = 'Service error';
                        });
                    }
                } else {
                    $scope.$apply(function () {
                        $scope.accErrorInfo = 'Service error';
                    });
                }
                $('#save-password-btn').each(function (item) {
                    item.disabled = true;
                })
            });
        }
    }]);