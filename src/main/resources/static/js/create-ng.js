angular.module('pokerApp', [])
    .controller('CreateController', ['$scope', 'filterFilter', function ObjectArrayCtrl($scope, filterFilter) {
        $scope.roomName = '';
        $scope.roles = roles;
        $scope.roleId = 0;
        $scope.days = 30;
        $scope.secretCodeRequired = false;
        $scope.secretCode = null;
        $scope.selection = [];
        $scope.nameInfoField = '';
        $scope.secretCodeInfoField = '';
        $scope.secretCodeInfoField = '';
        $scope.createErrorInfo = '';

        $scope.selectedRoles = function selectedRoles() {
            return filterFilter($scope.roles, { selected: true });
        };

        $scope.toggleSelection = function toggleSelection(role) {
            const idx = $scope.selection.indexOf(role);
            if (idx > -1) {
                $scope.selection.splice(idx, 1);
            }
            else {
                $scope.selection.push(role);
            }
        };

        $scope.doCreate = function () {
            $('#create-btn').each(function (item) {
                item.disabled = true;
            });
            $scope.nameInfoField = '';
            $scope.secretCodeInfoField = '';

            let roles = [];
            for(const selection of $scope.selection) {
                roles.push(selection.id);
            }
            if(roles.length === 0) {
                alert('Choose at least 1 role');
                return false;
            }
            if(!$scope.roleId) {
                alert('Choose your role');
                return false;
            }

            const secretCode = $scope.secretCodeRequired ? $scope.secretCode : null;

            $.ajax({
                method: "POST",
                url: "/api/room/createRoom",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({name: $scope.roomName, roles: roles, days: $scope.days,
                    roleId: $scope.roleId, secretCode: secretCode})
            })
            .done(function (msg) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        $scope.$apply(function () {
                            window.location.replace('/room/' + msg.roomId);
                        });
                    } else if (msg.resultCode === "Unauthorized") {
                        window.location.reload();
                    } else if (msg.resultCode === "ValidationError") {
                        $scope.$apply(function () {
                            for (const validationError of msg.validationErrors) {
                                if ("name" === validationError.field) {
                                    $scope.nameInfoField = validationError.message;
                                } else if ("secretCode" === validationError.field) {
                                    $scope.secretCodeInfoField = validationError.message;
                                } else {
                                    $scope.createErrorInfo = validationError.message;
                                }
                            }
                        });
                    } else {
                        $scope.createErrorInfo = "Service error";
                    }
                } else {
                    $scope.createErrorInfo = "Service error";
                }
                $('#create-btn').each(function (item) {
                    item.disabled = true;
                })
            });
        }
        return false;
    }]);