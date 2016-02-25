'use strict';

var app = angular.module('webEditor', ['ngWebsocket', 'ng-fusioncharts']);



app.run(function($rootScope, $websocket) {


  $rootScope.x = "bla";

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

      $rootScope.$apply();
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



app.controller('MainCtrl', ['$scope', function($scope) {

  //$scope.devices=[];


  //$scope.toSend = "test to send";

    //alert('jaja');

    /*$scope.chartDataSource = {
        data: [{
            value: "10"
        }, {
            value: "12"
        }, {
            value: "16"
        }, {
            value: "2"
        }, {
            value: "24"
        }, {
            value: "10"
        }, {
            value: "12"
        }, {
            value: "16"
        }, {
            value: "2"
        }, {
            value: "24"
        }, {
            value: "10"
        }, {
            value: "12"
        }, {
            value: "16"
        }, {
            value: "2"
        }, {
            value: "24"
        }, {
            value: "10"
        }, {
            value: "12"
        }, {
            value: "16"
        }, {
            value: "2"
        }, {
            value: "24"
        }, {
            value: "10"
        }, {
            value: "12"
        }, {
            value: "16"
        }]
    };

    $scope.attrs = {
        "caption": "Statistic for",
        "subCaption": "showing the current 24 hours",
        "numbersuffix": "°C",
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
    };

    $scope.categories = [{
        "category": [{
            "label": "00:00"
        }, {
            "label": "01:00"
        }, {
            "label": "02:00"
        }, {
            "label": "03:00"
        }, {
            "label": "04:00"
        }, {
            "label": "05:00"
        }, {
            "label": "06:00"
        }, {
            "label": "07:00"
        }, {
            "label": "08:00"
        }, {
            "label": "09:00"
        }, {
            "label": "10:00"
        }, {
            "label": "11:00"
        }, {
            "label": "12:00"
        }, {
            "label": "13:00"
        }, {
            "label": "14:00"
        }, {
            "label": "15:00"
        }, {
            "label": "16:00"
        }, {
            "label": "17:00"
        }, {
            "label": "18:00"
        }, {
            "label": "19:00"
        }, {
            "label": "20:00"
        }, {
            "label": "21:00"
        }, {
            "label": "22:00"
        }, {
            "label": "23:00"
        },
        ]
    }];*/

    $scope.attrs = {

        "caption": "Sales Comparison: 2013 versus 2014",
        "subCaption": "Harry’ s SuperMart",
    "numberprefix": "$",
    "plotgradientcolor": "",
    "bgcolor": "FFFFFF",
    "showalternatehgridcolor": "0",
    "divlinecolor": "CCCCCC",
    "showvalues": "0",
    "showcanvasborder": "0",
    "canvasborderalpha": "0",
    "canvasbordercolor": "CCCCCC",
    "canvasborderthickness": "1",
    "yaxismaxvalue": "30000",
    "captionpadding": "30",
    "linethickness": "3",
    "yaxisvaluespadding": "15",
    "legendshadow": "0",
    "legendborderalpha": "0",
    "palettecolors": "#f8bd19,#008ee4,#33bdda,#e44a00,#6baa01,#583e78",
    "showborder": "0"
};

$scope.categories = [{
    "category": [{
        "label": "Jan"
    }, {
        "label": "Feb"
    }, {
        "label": "Mar"
    }, {
        "label": "Apr"
    }, {
        "label": "May"
    }, {
        "label": "Jun"
    }, {
        "label": "Jul"
    }, {
        "label": "Aug"
    }, {
        "label": "Sep"
    }, {
        "label": "Oct"
    }, {
        "label": "Nov"
    }, {
        "label": "Dec"
    }]
}];

$scope.dataset = [{
    "seriesname": "2013",
    "data": [{
        "value": "22400"
    }, {
        "value": "24800"
    }, {
        "value": "21800"
    }, {
        "value": "21800"
    }, {
        "value": "24600"
    }, {
        "value": "27600"
    }, {
        "value": "26800"
    }, {
        "value": "27700"
    }, {
        "value": "23700"
    }, {
        "value": "25900"
    }, {
        "value": "26800"
    }, {
        "value": "24800"
    }]
},

    {
        "seriesname": "2012",
        "data": [{
            "value": "10000"
        }, {
            "value": "11500"
        }, {
            "value": "12500"
        }, {
            "value": "15000"
        }, {
            "value": "16000"
        }, {
            "value": "17600"
        }, {
            "value": "18800"
        }, {
            "value": "19700"
        }, {
            "value": "21700"
        }, {
            "value": "21900"
        }, {
            "value": "22900"
        }, {
            "value": "20800"
        }]
    }
];

  $scope.newConfig;
  $scope.newCondition = {};

  $scope.dname = "button1";
  $scope.editMode = true;
  $scope.newMode = true;

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



}]);
