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
    });

    $rootScope.sendToServer = function(toSend){
        ws.$emit("setDataType", toSend);
    }


});



app.controller('MainCtrl', ['$scope', function ($scope){


    //$scope.toSend = "test to send";
    $scope.dataType = "button";

    $scope.setDataType = function(){
        console.log('called setDataType');


        $scope.sendToServer($scope.dataType);
    }

    //$scope.test='not connected...';



}]);