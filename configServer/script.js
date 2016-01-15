
var WebSocketServer = require('ws').Server
    , controllerWSS = new WebSocketServer({ port: 50555 })
    , dataWSS = new WebSocketServer({ port : 50556});

var fs = require('fs');

/*
var Command = require('Command');
var Condition = require('Condition');
var Configuration = require('Configuration');
var Target = require('Target');
var DataDictEntry = require('DataDictEntry');*/

/*var parser = require(__dirname + '\\commandparser.js');
 var model = require(__dirname + '\\model.js');*/

var devices = [];


var configurations = [];
var conditions = [];
/*conditions.push(new Condition("time", "10:00", "lesser"));
conditions.push(new Condition("signal", true, "button"));
configurations.push(new Configuration(new Target("led1", "127.0.0.1", "on"), conditions, "and"));*/



fs.readFile( __dirname + "/" + "configurations.json", 'utf8', function (err, data) {
    console.log("read configuration data: " + data);

    //configurations = JSON.stringify(data);
    try{
        configurations = JSON.parse(data);
        console.log("valid json file: " + configurations);
    }
    catch(e){
        console.log("invalid or empty configurations.json file");
        return;
    }
    //console.log(JSON.stringify(configurations[0]));
    console.log("end of readFile");
});

var writeConfigsToFile = function (){




    fs.writeFile(__dirname + "/" + "configurations.json", JSON.stringify(configurations), function(err) {
        if(err) {
            return console.log(err);
        }

        console.log("changed config");
        console.log("current config: " + JSON.stringify(configurations));
    });
};


var addConfiguration = function (newConfig){
    configurations.push(newConfig);
    cleanupConfigurations();
    writeConfigsToFile();

};



var deleteConfiguration = function (toDelete){
    for(var i = 0; i < configurations.length; i++){
        if(configurations[i].cname === toDelete)
            configurations.splice(i, 1);
    }

    writeConfigsToFile();
};


var getIpForDeviceName = function (dname){

    for(var i = 0; i < devices.length; i++){
        if(devices[i].dname === dname)
            return device[i].ip;
    }
    return -1;
};

controllerWSS.on('connection', function connection(ws) {
  console.log('connected: %s', ws._socket.remoteAddress);
  ws.on('message', function incoming(message) {
      console.log("controllerWSS.onMessage: " + message);
      var pkg = JSON.parse(message);
      pkg.from = ws._socket.remoteAddress;
      pkg.dname = pkg.dname;
      pkg = JSON.stringify(pkg);

    //wss.sendToClient(ip, action);
      processCommand(JSON.parse(pkg));
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





var findConfigurationByName = function (cname){
    for(var i = 0; i < configurations.length; i++){
        if(configurations[i].cname === cname) return i;
    }

};

var editConfiguration = function (cname, newConfig) {

    configurations[findConfigurationByName(cname)] = newConfig;
    cleanupConfigurations();


    writeConfigsToFile();

};

var findUserEntry = function (ip){
   for(var i = 0; i < userDict.length; i++){
     if(userDict[i].to === ip) return i;
   }
    return -1;
};

var cleanupConfigurations = function(){
    //deletes the generated $$hashKey from configs
    for(var i = 0; i < configurations.length; i++){
        for(var j = 0; j < configurations[i].conditions.length; j++){
            delete configurations[i].conditions[j].$$hashKey;
        }

    }

    console.log("cleanup config called, current configs: " + JSON.stringify(configurations));
};

var findIpsForDeviceName = function (dname){

  var res = [];

  for(var i = 0; i < userDict.length; i++){
      if(userDict[i].dname === dname){
          console.log(JSON.stringify(userDict[i]));
          res.push(userDict[i].to);
          console.log("found matching ip for type: " + res[res.length-1]);
      }
  }

    return res;
};

var configNameAlreadyExists = function (cname){
    for(var i = 0; i < configurations.length; i++){
        if(configurations[i].cname === cname)
            return true;
    }
    return false;
};

dataWSS.on('connection', function connection(ws) {
    console.log('connected: %s', ws._socket.remoteAddress);

    ws.on('message', function incoming(message) {
        //console.log('received message: %s', message);
        //wss.sendToClient(ip, action);
        //console.log('WSSDATA RECEIVED: ' + message);

        msg = JSON.parse(message);


        console.log("dataWSS.onMessage: " + message);

        if(msg.event === "setDevice"){


            if(findUserEntry(ws._socket.remoteAddress) != -1){
                userDict.splice(findUserEntry(ws._socket.remoteAddress), 1);
                console.log("removed previous userDict (length: %d) entry for: " + ws._socket.remoteAddress, userDict.length);
            }

            userDict.push({
                to:ws._socket.remoteAddress,
                dname:msg.data
            });
            console.log("added new userDict entry: " + JSON.stringify(userDict[userDict.length-1]));
        }


        if(msg.event === "getDevices"){
            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"devices\": " + JSON.stringify(devices) + "}");
        }


        if(msg.event === "newConfiguration"){
            console.log("newConfiguration called with data: ");
            console.log(JSON.stringify(msg.data));


            if(configNameAlreadyExists(msg.data.cname)){
                dataWSS.sendToClient(ws._socket.remoteAddress, JSON.stringify({
                    event:"error",
                    data:"configuration name already exists"
                }));
                return;
            }

            var obj = msg.data;
            console.log("saving this to configurations (new): " + JSON.stringify(obj));

            addConfiguration(msg.data);
            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"configurations\":" + JSON.stringify(configurations) + "}");


        }

        if(msg.event === "deleteConfiguration"){
            console.log("deleteConfiguration called with data: ");
            console.log(JSON.stringify(msg.data));

            deleteConfiguration(msg.data.cname);
        }

        if(msg.event === "getConfigurations"){
            console.log("getConfiguration called with data: ");
            console.log(JSON.stringify(msg.data));


            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"configurations\":" + JSON.stringify(configurations) + "}");
        }


        if(msg.event === "editConfiguration"){
            console.log("editConfiguration called with data: " + msg.data);
            var obj = msg.data;
            delete obj.$$hashKey;
            console.log("saving this to configurations (edit): " + JSON.stringify(obj));

            editConfiguration(obj.cname, obj);



            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"configurations\":" + JSON.stringify(configurations) + "}");
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

    if(tmp.event === "regDevice") return;


    console.log("checkAndSendData called with message: " + message);
    console.log("parsed json: " + tmp.dname + " " + tmp.data);


    var ips = findIpsForDeviceName(tmp.dname);



    for(var i = 0; i < ips.length; i++){

        dataWSS.sendToClient(ips[i], JSON.stringify(tmp));
        console.log("sent data: " + tmp.data + " to ip: " + ips[i]);
    }

};


/*
var parseMessage = function (msg){
    var tmp = JSON.parse(msg);
    console.log("called parseMessage from: " + tmp.from + " data: " + tmp.data + " type: " + tmp.type);
    //return new Command(tmp.from, tmp.type, tmp.data);
    return {
        from:tmp.from,
        type:tmp.type,
        data:tmp.data,
        event:tmp.event,

    };
};
*/


var processCommand = function (cmd){
    console.log("processCommand called");

    if(cmd.event === "regDevice"){
        devices.push({
            type:cmd.type,
            category:cmd.category,
            dname:cmd.dname,
            values:cmd.values,
            ip:cmd.from
        });
        console.log("device registered: " + JSON.stringify(devices[devices.length-1]));
        dataWSS.sendToClient(cmd.from, "{\"devices\": " + JSON.stringify(devices) + "}");

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

var getTypeForDevice = function(dname){
      for(var i = 0; i < devices.length; i++){
          if(devices[i].dname === dname) return devices[i].type;
      }
};

var checkConfigurations = function (cmd){

    var flag = false;
    console.log("checkConfigurations called:" + JSON.stringify(cmd));

    for(var i = 0; i < configurations.length; i++){
        console.log("configurations length: " + configurations.length);
        var config = configurations[i];
        for(var j = 0; j < configurations[i].conditions.length; j++) {
            console.log("conditions length: " + configurations[i].conditions.length);
            var cnd = configurations[i].conditions[j];

            if(cnd.dname === "time")
                flag = checkTime(cnd);
            if(getTypeForDevice(cnd.dname) === "button")
                flag = applyLogicalOperator(cmd.data, flag, config.logicaloperator);

            console.log("loop count (%d,%d): flag value: %s", i, j, flag);
        }



        if(flag){
            controllerWSS.sendToClient(config.target.ip, '{"event":"message","data":"activate"}');

        }

        flag = false;
    }

};
