'use strict';

var app = angular.module('webEditor', ['ngWebsocket']);




app.run(function ($rootScope, $websocket){


    $rootScope.x = "bla";

    var ws = $websocket.$new({
        url: 'ws://localhost:50556',
        reconnect: true // it will reconnect after 2 seconds
    });


    ws.$on('$open', function(){
        console.log('connected!!');

        $rootScope.x = "connected";
        $rootScope.$apply();
        console.log('ws.onopen, $rootScope.test: ' + $rootScope.x);

    });

    ws.$on('$message', function (data) {
        console.log('onMessage called with: ' + JSON.stringify(data));
        $rootScope.x = "received message: " + JSON.stringify(data);
        $rootScope.$apply();
        var jdata = JSON.parse(data);

        if(jdata.hasOwnProperty("devices")){
            $rootScope.devices = jdata.devices;
            $rootScope.$apply();
        }

    });

    $rootScope.sendToServer = function(event, toSend){
        ws.$emit(event, toSend);
    }


});



app.controller('MainCtrl', ['$scope', function ($scope){

    //$scope.devices=[];


    //$scope.toSend = "test to send";
    $scope.dataType = "button";

    $scope.setDataType = function(){
        console.log('called setDataType');


        $scope.sendToServer("setDataType", $scope.dataType);
    };

    $scope.getDevices = function() {
        console.log("called getDevices");
        $scope.sendToServer("getDevices","");
    }

    //$scope.test='not connected...';



}]);