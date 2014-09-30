'use strict';

tmfNotificationApp.controller('ListController', ['$scope', 'History', function($scope, History) {
        $scope.notifications = History.query();
    }]);

tmfNotificationApp.controller('CurrentController', ['$scope', 'currentService', function($scope, currentService) {
        currentService.getCurrentNotification(function(notification){
            $scope.notification = notification;
        });
    }]);


tmfNotificationApp.controller('formatJsonCtrl', ['$scope', function($scope) {

//        var jsonString = $scope.notification.resource;
        var jsonString = '{"some":"json"}';
        var jsonPretty;
        if (typeof jsonString !== 'string') {
            jsonPretty = JSON.stringify(jsonString, undefined, 2);
        } else {
            jsonPretty = JSON.stringify(JSON.parse(jsonString), null, 2);
        }
//        var reg=new RegExp("([,])", "g");
//        jsonString.replace(reg,"$1\n");
        $scope.notification.resource = jsonPretty;
    }]);

tmfNotificationApp.controller('ModalCtrl', ['$scope', '$modal', function($scope, $modal) {

        $scope.items = [$scope.$parent.notification.resource, 'TOTO'];
        $scope.notification = $scope.$parent.notification.resource;

        $scope.open = function() {

            var modalInstance = $modal.open({
                restrict: 'E',
                templateUrl: 'view/myModal.html',
                controller: ModalInstanceCtrl,
                transclude: true,
                resolve: {
                    jsonString: function() {
                        return $scope.notification;
                    }
                }
            });
        };
    }]);

var ModalInstanceCtrl = ['$scope', '$modalInstance', 'jsonString', function($scope, $modalInstance, jsonString) {

        //var str = angular.toJson(editResource);
        //var jsonPretty = JSON.stringify(editResource, undefined, 2); ([\\]\\[{},]);
        //jsonString = '{"some":"json"}';
        var jsonPretty;
        if (typeof jsonString != 'string') {
            jsonPretty = JSON.stringify(jsonString, undefined, 2);
        } else {
            jsonPretty = JSON.stringify(JSON.parse(jsonString, function(k, v) {
                return v + "<BR>";
            }), null, 2);
        }
//        var reg=new RegExp("([,])", "g");
//        jsonString.replace(reg,"$1\n");
        $scope.notification = jsonPretty;
        $scope.ok = function() {
            $modalInstance.close($scope.notification);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    }];


