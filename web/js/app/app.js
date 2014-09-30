'use strict';

var tmfNotificationApp = angular.module('tmfNotificationApp', ['ngResource','ngRoute', 'ui.bootstrap', 'tmfNotificationServices', 'tmfNotificationFilters']);

tmfNotificationApp.config(function ($routeProvider) {
    $routeProvider.when('/list', {templateUrl: 'view/list.html',     controller: 'ListController'});
    $routeProvider.when('/viewCurrent', {templateUrl: 'view/viewCurrent.html',   controller: 'CurrentController'});
    $routeProvider.otherwise({redirectTo: '/list'});
});

tmfNotificationApp.run(function ($rootScope, $location) {
    $rootScope.location = $location;
});
