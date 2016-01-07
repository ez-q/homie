
var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ port: 50555 });




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
configurations.push(new Configuration(new Target("test", "127.0.0.1"), conditions));

//console.log(configurations.conditions.length);

function Command(from, type, data){
    this.from = from;
    this.data = data;
    this.type = type;
}

function Condition(type, value, mod){
    this.type = type;
    this.value = value;
    this.mod = mod;
}

function Target(name, ip){
    this.name = name;
    this.ip = ip;
}

function Configuration(target, conditions){
    this.target = target;
    this.conditions = conditions;
}

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

var checkConfigurations = function (cmd){

    var flag = false;
    console.log("checkConfigurations called");

    for(var i = 0; i < configurations.length; i++){
        console.log("configurations length: " + configurations.length);
        for(var j = 0; j < configurations[i].conditions.length; j++) {
            console.log("conditions length: " + configurations[i].conditions.length);
            var cnd = configurations[i].conditions[j];

            if(cnd.type === "time")
                flag = checkTime(cnd);
            if(cnd.type === "button")
                flag = cmd.data && flag;

            console.log("loop count (%d,%d): flag value: %s", i, j, flag);
        }

        var config = configurations[i];

        if(flag){
            wss.sendToClient(config.target.ip, "ACTIVATE YE BOY");
        }

        flag = false;
    }

};
