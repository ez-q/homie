
var WebSocketServer = require('ws').Server
    , controllerWSS = new WebSocketServer({ port: 50555 })
    , dataWSS = new WebSocketServer({ port : 50556});


var Command = require('Command');
var Condition = require('Condition');
var Configuration = require('Configuration');
var Target = require('Target');
var DataDictEntry = require('DataDictEntry');

/*var parser = require(__dirname + '\\commandparser.js');
 var model = require(__dirname + '\\model.js');*/

var devices = [];


controllerWSS.on('connection', function connection(ws) {
  console.log('connected: %s', ws._socket.remoteAddress);
  ws.on('message', function incoming(message) {
	console.log('received message: %s', message);
      var pkg = JSON.parse(message);
      pkg.from = ws._socket.remoteAddress;
      pkg = JSON.stringify(pkg);
    //wss.sendToClient(ip, action);
      processCommand(parseMessage(pkg));
      checkAndSendData(pkg);
  });

  ws.send('connected');
});

controllerWSS.sendToClient = function sendToClient(address, data){
	controllerWSS.clients.forEach(function each(client){
		if(client._socket.remoteAddress === address){
			client.send(data);
		}
	});
};



var userDict = [];
var dataDict = {};

/*var devices = [
    {
        type:"button",
        name:"button1",
        values:[true, false]
    },
    {
        type:"button",
        name:"button2",
        values:[true,false]
    }

];*/



var findUserEntry = function (ip){
   for(var i = 0; i < userDict.length; i++){
     if(userDict[i].to === ip) return i;
   }
    return -1;
};

var findIpsForDataType = function (dataType){

  var res = [];

  for(var i = 0; i < userDict.length; i++){
      if(userDict[i].type === dataType){
          console.log(JSON.stringify(userDict[i]));
          res[res.length] = userDict[i].to;
          console.log("found matching ip for type: " + res[res.length-1]);
      }
  }

    return res;
};

dataWSS.on('connection', function connection(ws) {
    console.log('connected: %s', ws._socket.remoteAddress);

    ws.on('message', function incoming(message) {
        //console.log('received message: %s', message);
        //wss.sendToClient(ip, action);
        console.log('WSSDATA RECEIVED: ' + message);

        msg = JSON.parse(message);


        if(msg.event === "setDataType"){


            if(findUserEntry(ws._socket.remoteAddress) != -1){
                userDict.splice(findUserEntry(ws._socket.remoteAddress), 1);
                console.log("removed previous userDict (length: %d) entry for: " + ws._socket.remoteAddress, userDict.length);
            }

            userDict.push(new DataDictEntry(ws._socket.remoteAddress, msg.data));
            console.log("added new userDict entry: " + JSON.stringify(userDict[userDict.length-1]));
        }


        if(msg.event === "getDevices"){
            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"devices\": " + JSON.stringify(devices) + "}");
        }


        if(msg.event === "newConfiguration"){



        }



    });

    ws.send('connected');
});

dataWSS.sendToClient = function sendToClient(address, data){
    dataWSS.clients.forEach(function each(client){
        if(client._socket.remoteAddress === address){
            client.send(data);
        }
    });
};



var checkAndSendData = function (message) {

    var tmp = JSON.parse(message);

    if(tmp.type === "regDevice") return;


    console.log("checkAndSendData called with message: " + message);
    console.log("parsed json: " + tmp.type + " " + tmp.data);


    var ips = findIpsForDataType(tmp.type);



    for(var i = 0; i < ips.length; i++){

        dataWSS.sendToClient(ips[i], JSON.stringify(tmp));
        console.log("sent data: " + tmp.data + " to ip: " + ips[i]);
    }

};

var configurations = [];
var conditions = [];
conditions.push(new Condition("time", "10:00", "lesser"));
conditions.push(new Condition("signal", true, "button"));
configurations.push(new Configuration(new Target("led1", "127.0.0.1", "on"), conditions, "and"));

var parseMessage = function (msg){
    var tmp = JSON.parse(msg);
    console.log("called parseMessage from: " + tmp.from + " data: " + tmp.data + " type: " + tmp.type);
    return new Command(tmp.from, tmp.type, tmp.data);
};



var processCommand = function (cmd){
    console.log("processCommand called");

    if(cmd.type === "regDevice"){
        devices.push({
            type:cmd.actType,
            name:cmd.name,
            values:cmd.values,
            ip:cmd.from
        });
        console.log("device registered: " + JSON.stringify(devices[devices.length-1]));
    }
    else {
        checkConfigurations(cmd);
    }
};

var checkTime = function (cnd){
    var currDate = new Date();
    var checkDate = new Date();
    checkDate.setHours(cnd.value.split(":")[0]);
    checkDate.setMinutes(cnd.value.split(":")[1]);
    checkDate.setSeconds(0);

    console.log("checkTime called");
    console.log("currDate: " + currDate);
    console.log("checkDate: " + checkDate);

    console.log("cnd.mod: " + cnd.mod);

    if(cnd.mod === "greater")
        return checkDate >= currDate;
    if(cnd.mod === "lesser")
        return checkDate <= currDate;
};

var applyLogicalOperator = function (value, flag, operator){
    if(operator === "and")
        return value && flag;
    if(operator === "or")
        return value || flag;
    else
        return false;
};

var checkConfigurations = function (cmd){

    var flag = false;
    console.log("checkConfigurations called");

    for(var i = 0; i < configurations.length; i++){
        console.log("configurations length: " + configurations.length);
        var config = configurations[i];
        for(var j = 0; j < configurations[i].conditions.length; j++) {
            console.log("conditions length: " + configurations[i].conditions.length);
            var cnd = configurations[i].conditions[j];

            if(cnd.type === "time")
                flag = checkTime(cnd);
            if(cnd.type === "button")
                flag = applyLogicalOperator(cmd.data, flag, config.logicaloperator);

            console.log("loop count (%d,%d): flag value: %s", i, j, flag);
        }



        if(flag){
            controllerWSS.sendToClient(config.target.ip, '{"event":"message","data":"activate"}');

        }

        flag = false;
    }

};
