angular.module('pokerApp', [])
    .controller('RoomController', function($scope) {
        $scope.roomInfo = roomInfo;
        $scope.scores = ['0', '1/2', '1', '2', '3', '5', '8', '13', '?', '☕'];

        $scope.isUserScore = function (score) {
            return $scope.roomInfo.score == score;
        };

        $scope.anyVoted = function (groupInfo) {
            for (const participantInfo of groupInfo.participantInfos) {
                if (participantInfo.voted) {
                    return true;
                }
            }
            return false;
        };

        $scope.vote = function (score) {
            $.ajax({
                method: "POST",
                url: "/api/room/vote",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({roomId: $scope.roomInfo.id, score: score})
            })
                .done(function (msg) {
                    if (msg != null) {
                        if (msg.resultCode === "Ok") {
                            $scope.$apply(function () {
                                $scope.roomInfo.score = score;
                            });
                        } else if (msg.resultCode === "Unauthorized") {
                            window.location.reload();
                        } else {
                            alert("Service error");
                        }
                    } else {
                        alert("Service error");
                    }
                });
        }

        $scope.setVoting = function () {
            $('.action-btn').each(function (item) {
                item.disabled = true;
            })
            const voting = !$scope.roomInfo.voting;

            $.ajax({
                method: "POST",
                url: "/api/room/setVoting",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({roomId: $scope.roomInfo.id, voting: voting})
            })
            .done(function (msg) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        $scope.$apply(function () {
                            $scope.roomInfo = msg.roomInfo;
                        });
                    } else if (msg.resultCode === "Unauthorized") {
                        window.location.reload();
                    } else {
                        alert("Service error");
                    }
                } else {
                    alert("Service error");
                }
                $('.action-btn').each(function (item) {
                    item.disabled = false;
                })
            });
        }

        $scope.updateData = function () {
            $.ajax({
                method: "POST",
                url: "/api/room/getRoom",
                headers: {'X-Session-Id': sessionId},
                contentType: "application/json",
                data: JSON.stringify({roomId: $scope.roomInfo.id})
            })
            .done(function (msg) {
                if (msg != null) {
                    if (msg.resultCode === "Ok") {
                        $scope.$apply(function () {
                            $scope.roomInfo = msg.roomInfo;
                        });
                    } else if (msg.resultCode === "Unauthorized") {
                        window.location.reload();
                    } else {
                        alert("Service error");
                    }
                } else {
                    alert("Service error");
                }
            });
            setTimeout($scope.updateData, 3000);
        }

        $scope.updateData();
    });