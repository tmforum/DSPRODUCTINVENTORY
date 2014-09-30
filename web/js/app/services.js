'use strict';

var tmfNotificationServices = angular.module('tmfNotificationServices', ['ngResource']);

tmfNotificationServices.factory('History', function($resource) {
    return $resource('http://localhost:8091/history');
});

tmfNotificationServices.factory('Current', function($resource) {
    return $resource('http://localhost:8091/current');
});


tmfNotificationServices.service('currentService', function($http) {
    this.getCurrentNotification = function(callback) {
        $http({
            method: 'GET',
            url: 'http://localhost:8091/current',
            isArray: false
        }).success(function(notification) {
            console.log("SUCCESS" + notification.eventType);
            callback(notification);
        }).error(function() {
            console.log("ERROR!");
        });
    };
});
