'use strict';

var app = angular.module('webEditor', ['ngWebsocket', 'ng-fusioncharts']);



app.run(function($rootScope, $websocket) {


  $rootScope.x = "bla";
  $rootScope.tempHistData;

  var ws = $websocket.$new({
    url: 'ws://localhost:50556',
    reconnect: true // it will reconnect after 2 seconds
  });


  ws.$on('$open', function() {
    console.log('connected!!');

    $rootScope.x = "connected";
    $rootScope.$apply();
    console.log('ws.onopen, $rootScope.test: ' + $rootScope.x);

    ws.$emit("getDevices", "");
    ws.$emit("getConfigurations", "");
    ws.$emit("subscribeLatestData", "");

  });

  ws.$on('$message', function(data) {
    console.log('onMessage called with: ' + JSON.stringify(data));
    $rootScope.x = "received message: " + JSON.stringify(data);
    var jdata = JSON.parse(data);



    if ('devices' in jdata) {
      $rootScope.devices = jdata.devices;
      $rootScope.actors = [];
      $rootScope.sensors = [];

      console.log("on message: devices");


      for (var i = 0; i < jdata.devices.length; i++) {
        if (jdata.devices[i].category === "actor") $rootScope.actors.push(
          jdata.devices[i]);
        if (jdata.devices[i].category === "sensor") $rootScope.sensors.push(
          jdata.devices[i]);
        console.log("actors, sensors: $d $d", $rootScope.actors.length,
          $rootScope.sensors.length);
      }
      console.log(JSON.stringify($rootScope.sensors));
      $rootScope.$apply();
    }

    if ('historyData' in jdata) {
      $rootScope.tempHistData = jdata;
      $rootScope.$apply();
      console.log("tempHistData " + JSON.stringify($rootScope.tempHistData));
      $rootScope.setChartData($rootScope.tempHistData, jdata.dname);
    }

    if ('configurations' in jdata) {


      console.log("on message: configurations");

      $rootScope.configurations = jdata.configurations;
      $rootScope.$apply();

    }


    if (jdata.hasOwnProperty("event")) {
      if (jdata.event === "error")
        $rootScope.errorLog = jdata.data;
    }

    $rootScope.$apply();

  });



  $rootScope.sendToServer = function(event, toSend) {
    console.log("sendToServer called: %s - %s", event, toSend);
    ws.$emit(event, toSend);
  };


});



app.controller('MainCtrl', ['$scope', '$rootScope', function($scope, $rootScope) {

  //$scope.devices=[];

  //$scope.sensors = [];


  //$scope.toSend = "test to send";

  //alert('jaja');

  /*$scope.tempHistData = {
    "dname": "button1",
    "historyData": [{
      "value": 10,
      "date": "1456176227162.0"
    }, {
      "value": 15,
      "date": "1456176231889.0"
    }, {
      "value": 20,
      "date": "1456176234578.0"
    }, {
      "value": 15,
      "date": "1456176231889.0"
    }, {
      "value": 20,
      "date": "1456186234578.0"
    }]
  };*/

  $scope.chartDataSource = {
    "chart": {
      "caption": "Placeholder",
      "numbersuffix": " C",
      "plotgradientcolor": "",
      "bgcolor": "FFFFFF",
      "showalternatehgridcolor": "0",
      "divlinecolor": "CCCCCC",
      "showvalues": "0",
      "showcanvasborder": "0",
      "canvasborderalpha": "0",
      "canvasbordercolor": "CCCCCC",
      "canvasborderthickness": "1",
      "yaxismaxvalue": "35",
      "captionpadding": "30",
      "linethickness": "3",
      "yaxisvaluespadding": "15",
      "legendshadow": "0",
      "legendborderalpha": "0",
      "palettecolors": "#f8bd19,#008ee4,#33bdda,#e44a00,#6baa01,#583e78",
      "showborder": "0"
    },
    data: []
  };



  $scope.newConfig;
  $scope.newCondition = {};

  $scope.dname = "button1";
  $scope.editMode = true;
  $scope.newMode = true;

  $scope.showStat = false;

  $scope.saveCurrConditionToConfiguration = function() {
    $scope.x = 'saveCurrConditionToConfiguration called';
    alert('saveCurrConditionToConfiguration called');

    if ($scope.newCondition.value === "true" || $scope.newCondition.value ===
      "on") $scope.newCondition.value = true;
    if ($scope.newCondition.value === "false" || $scope.newCondition.value ===
      "off") $scope.newCondition.value = false;

    $scope.newConfig.conditions.push($scope.newCondition);
    $scope.newCondition = {};
  };

  $scope.saveConfiguration = function() {
    if ($scope.newConfig.conditions.length <= 0) return;
    if ($scope.newConfig.action === "true" || $scope.newConfig.action ===
      "on") $scope.newCondition.value = true;
    if ($scope.newConfig.action === "false" || $scope.newConfig.action ===
      "off") $scope.newCondition.value = false;
    if ($scope.newMode) {
      $scope.sendToServer("newConfiguration", $scope.newConfig);
      $scope.newConfig = {};
    }
    if (!$scope.newMode) {
      $scope.sendToServer("editConfiguration", $scope.newConfig);
      $scope.newConfig = {};
    }

  };

  $rootScope.setChartData = function(history, device) {
    $scope.chartDataSource.chart.caption = "History for " + device;
    $scope.chartDataSource.data.splice(0, $scope.chartDataSource.data.length);
    //if (device.type === "temperature") {
    $scope.chartDataSource.chart.suffix = " C";
    for (var i = 0; i < history.historyData.length; i++) {
      var dat = new Date();
      dat.setTime(history.historyData[i].date);
      $scope.chartDataSource.data.push({
        "value": history.historyData[i].value,
        "label": dat.getHours().toString() + ":" + dat.getMinutes()
          .toString()
      });
      //}
    }
  }

  $scope.show = function(device) {
    $scope.sendToServer("getHistoryDataByDname", {
      dname: device.dname
    });
    $scope.showStat = true;
  }

  $scope.setDevice = function() {
    console.log('called setDevice');


    $scope.sendToServer("setDevice", $scope.dname);
  };


  $scope.editConfiguration = function(config) {
    //angular.copy(config, $scope.newConfig);
    $scope.newConfig = config;
    console.log($scope.newConfig);
    $scope.editMode = false;
    $scope.newMode = false;
  };

  $scope.deleteConfiguration = function(config) {
    $scope.sendToServer("deleteConfiguration", {
      cname: config.cname
    });
  };

  $scope.setNewConfig = function() {
    $scope.newConfig = {
      conditions: []
    };
    $scope.editMode = false;
    $scope.newMode = true;
  };

  $scope.sendOnCommand = function(actor) {
    $scope.sendToServer("forceDeviceToExecuteCommand", {
      dname: actor.dname,
      action: true
    });
  };

  $scope.sendOffCommand = function(actor) {
    $scope.sendToServer("forceDeviceToExecuteCommand", {
      dname: actor.dname,
      action: false
    });
  };

  /*



      $scope.getDevices = function() {
          console.log("called getDevices");
          $scope.sendToServer("getDevices","a");
      };

      $scope.getDevices();*/

  //$scope.test='not connected...';
  /*$scope.sensors.push({
      "type": "temperature",
      "category": "sensor",
      "dname": "button1",
  });*/

}]);
