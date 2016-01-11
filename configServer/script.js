
var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ port: 50555 });

var Command = require('Command');
var Condition = require('Condition');
var Configuration = require('Configuration');
var Target = require('Target');

/*var parser = require(__dirname + '\\commandparser.js');
var model = require(__dirname + '\\model.js');*/


wss.on('connection', function connection(ws) {
  console.log('connected: %s', ws._socket.remoteAddress);
  ws.on('message', function incoming(message) {
	console.log('received message: %s', message);
    //wss.sendToClient(ip, action);
      processCommand(parseMessage(message));
  });

  ws.send('connected');
});

wss.sendToClient = function sendToClient(address, data){
	wss.clients.forEach(function each(client){
		if(client._socket.remoteAddress === address){
			client.send(data);
		}
	});
};


var configurations = [];
var conditions = [];
conditions.push(new Condition("time", "10:00", "lesser"));
conditions.push(new Condition("signal", true, "button"));
configurations.push(new Configuration(new Target("led1", "127.0.0.1", "on"), conditions, "AND"));

var parseMessage = function (msg){
    var tmp = JSON.parse(msg);
    console.log("called parseMessage - from: " + tmp.from + " data: " + tmp.data + " type: " + tmp.type);
    return new Command(tmp.from, tmp.type, tmp.data);
};



var processCommand = function (cmd){
    console.log("processCommand called");
    checkConfigurations(cmd);
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
    if(operator === "AND")
        return value && flag;
    if(operator === "OR")
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
           // wss.sendToClient(config.target.ip, "ACTIVATE YE BOY");
            wss.sendToClient(config.target.ip, '{"event":"message","data":"activate"}');

        }

        flag = false;
    }

};
