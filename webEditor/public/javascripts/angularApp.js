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

        ws.$emit("getDevices", "");
        ws.$emit("getConfigurations","");

    });

    ws.$on('$message', function (data) {
        console.log('onMessage called with: ' + JSON.stringify(data));
        $rootScope.x = "received message: " + JSON.stringify(data);
        var jdata = JSON.parse(data);



        if('devices' in jdata){
            $rootScope.devices = jdata.devices;
            $rootScope.actors = [];
            $rootScope.sensors = [];

            console.log("on message: devices");


            for(var i = 0; i < jdata.devices.length; i++){
                if(jdata.devices[i].category === "actor") $rootScope.actors.push(jdata.devices[i]);
                if(jdata.devices[i].category === "sensor") $rootScope.sensors.push(jdata.devices[i]);
                console.log("actors, sensors: $d $d",$rootScope.actors.length,$rootScope.sensors.length);
            }
        }


        if('configurations' in jdata){


            console.log("on message: configurations");

            $rootScope.configurations = jdata.configurations;
            $rootScope.$apply();

        }


        if(jdata.hasOwnProperty("event")){
            if(jdata.event === "error")
                $rootScope.errorLog = jdata.data;
        }

        $rootScope.$apply();

    });



    $rootScope.sendToServer = function(event, toSend){
        console.log("sendToServer called: %s - %s", event, toSend);
        ws.$emit(event, toSend);
    };


});



app.controller('MainCtrl', ['$scope', function ($scope){

    //$scope.devices=[];


    //$scope.toSend = "test to send";

    //alert('jaja');

    $scope.newConfig;
    $scope.newCondition;

    $scope.dname = "button1";
    $scope.editMode = true;
    $scope.newMode = true;

    $scope.saveCurrConditionToConfiguration = function(){
        $scope.x = 'saveCurrConditionToConfiguration called';
        alert('saveCurrConditionToConfiguration called');

        if($scope.newCondition.value === "true" || $scope.newCondition.value === "on") $scope.newCondition.value = true;
        if($scope.newCondition.value === "false" || $scope.newCondition.value === "off") $scope.newCondition.value = false;

        $scope.newConfig.conditions.push($scope.newCondition);
        $scope.newCondition = {};
    };

    $scope.saveConfiguration = function(){
        if($scope.newConfig.conditions.length <= 0)  return;
        if($scope.newConfig.action === "true" || $scope.newConfig.action === "on") $scope.newCondition.value = true;
        if($scope.newConfig.action === "false" || $scope.newConfig.action === "off") $scope.newCondition.value = false;
        if($scope.newMode){
            $scope.sendToServer("newConfiguration", $scope.newConfig);
            $scope.newConfig = {};
        }
        if(!$scope.newMode){
            $scope.sendToServer("editConfiguration", $scope.newConfig);
            $scope.newConfig = {};
        }

    };

    $scope.setDevice = function(){
        console.log('called setDevice');


        $scope.sendToServer("setDevice", $scope.dname);
    };


    $scope.editConfiguration = function(config){
        console.log(config);
        $scope.newConfig = config;
        $scope.editMode = false;
        $scope.newMode = false;
    };

    $scope.setNewConfig = function(){
        $scope.newConfig = { conditions: []};
        $scope.editMode = false;
        $scope.newMode = true;
    };



/*



    $scope.getDevices = function() {
        console.log("called getDevices");
        $scope.sendToServer("getDevices","a");
    };

    $scope.getDevices();*/

    //$scope.test='not connected...';



}]);
