'use strict';

var CONTROLLER_PORT = 50555;
var DATA_PORT = 50556;
var CONFIGURATIONS_PATH = __dirname + "/" + "configurations.json";


var WebSocketServer = require('ws').Server
    , controllerWSS = new WebSocketServer({ port: CONTROLLER_PORT })
    , dataWSS = new WebSocketServer({ port : DATA_PORT});

var fs = require('fs');

//database stuff
var file = "DataLog.db";
var exists = fs.existsSync(file);

var sqlite3 = require("sqlite3").verbose();

var db = new sqlite3.Database(file);


db.serialize(function() {
  if(!exists){
      db.run("CREATE TABLE DATA (DNAME TEXT NOT NULL, VALUE TEXT, DATE TEXT NOT NULL);");
    }
});

/*
db.serialize(function() {
  if(!exists){
    db.run("CREATE TABLE DATA (DNAME TEXT NOT NULL, VALUE TEXT, DATE TEXT NOT NULL);");
  }

  var stmt = db.prepare("INSERT INTO DATA VALUES(?,?,?);");

  stmt.run("button1", "true", new Date().getTime());

  stmt.finalize();

  db.all("SE.forLECT dname, value, date from DATA", function(err, rows){
    console.log(rows);
    rows.forEach(function(row){
      console.log(row);
      console.log("dname: %s - value: %s - date: %s", row.DNAME, row.VALUE, row.DATE);
    })

  });

});*/

var writeDataToDb = function (dname, value) {

  db.serialize(function() {


    var stmt = db.prepare("INSERT INTO DATA VALUES(?,?,?);");

    stmt.run(dname, value, new Date().getTime());

    stmt.finalize();
  });
};


var sendHistoryDataToClientByDname = function (dname, ip){
  var res = [];



  db.serialize(function() {

    /*if(!exists){
      console.log('db doesnt exist cant read data');
      return;
    }*/




    //console.log(srch);


    db.all('SELECT value, date from DATA where DNAME = \'' + dname + '\';', function(err, rows){
      //console.log(rows);

      rows.forEach(function(row){

        //console.log(row);
        var tmp = row;
        //delete tmp.DNAME;
        if(tmp.VALUE == 1){
          tmp.value = true;
        }else{
          tmp.value = false;
        }
        delete tmp.VALUE;

        tmp.date = tmp.DATE;
        delete tmp.DATE;

        //console.log(tmp.value);
        res.push(tmp);
        console.log(JSON.stringify(res));

      /*  sendHistoryDataToClient(res, ip);

        return res;*/
      })
      var obj = {
        dname:dname,
        historyData:res
      }

      dataWSS.sendToClient(ip, JSON.stringify(obj));

    });


  });
  //console.log('res: ' + JSON.stringify(res));

};


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


//loads all configurtaions from the configurations.json file
fs.readFile(CONFIGURATIONS_PATH, 'utf8', function (err, data) {
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

      cleanupConfigurations();



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

    writeConfigsToFile();

};



var deleteConfiguration = function (toDelete){
    for(var i = 0; i < configurations.length; i++){
        if(configurations[i].cname === toDelete)
            configurations.splice(i, 1);
    }

    writeConfigsToFile();
};

//param: device for which the ip should be found
//returns: found: ip from that device, not found -1
var getIpForDeviceName = function (dname){

    for(var i = 0; i < devices.length; i++){
        if(devices[i].dname === dname)
            return devices[i].ip;
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


      //processes the command
      processCommand(pkg);

      //checks if a user has registered for data from this device, if yes data will be sent to that device
      //checkAndSendData(pkg);
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

//deletes the generated $$hashKey by angular
//which is generated by using a nested ng-repeat
var cleanupConfigurations = function(){
    for(var i = 0; i < configurations.length; i++){
        for(var j = 0; j < configurations[i].conditions.length; j++){
            delete configurations[i].conditions[j].$$hashKey;
        }

    }

    console.log("cleanup config called, current configs: " + JSON.stringify(configurations));
};

//searches the userDict registered devices' ip addresses
var findIpsForDeviceName = function (dname){

  var res = [];

  for(var i = 0; i < userDict.length; i++){
      if(userDict[i].dname === dname){
          console.log(JSON.stringify(userDict[i]));
          res.push(userDict[i].to);
          console.log("found matching ip for dname: " + res[res.length-1]);
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

//this socket controls the data
dataWSS.on('connection', function connection(ws) {
    console.log('connected: %s', ws._socket.remoteAddress);

    ws.on('message', function incoming(message) {
        //console.log('received message: %s', message);
        //wss.sendToClient(ip, action);
        //console.log('WSSDATA RECEIVED: ' + message);

        var msg = JSON.parse(message);


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

            dataWSS.sendToClient(ws._socket.remoteAddress, "{\"configurations\":" + JSON.stringify(configurations) + "}");

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

        if(msg.event === "getHistoryDataByDname"){
          console.log("getHistoryDataByDname called with data: " + JSON.stringify(msg.data));

          sendHistoryDataToClientByDname(msg.data.dname, ws._socket.remoteAddress);


        }

        //the client has to have the device registered in the userDict for this to work
        //that means first call setDataType then call forceDeviceToSendData for the client to receive the current data
        if(msg.event === "forceDeviceToSendData"){
          var dname = msg.data.dname;

          var ip = getIpForDeviceName(dnameToForceDataFrom);

          var obj = {
            event:"send_data",
            data:{}
          };
          controllerWSS.sendToClient(ip, JSON.stringify(obj));

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

    var tmp = message;

    //if(tmp.event === "regDevice") return;


    console.log("checkAndSendData called with message: " + message);
    console.log("parsed json: " + tmp.dname + " " + tmp.data);


    var ips = findIpsForDeviceName(tmp.dname);



    for(var i = 0; i < ips.length; i++){

        dataWSS.sendToClient(ips[i], JSON.stringify(tmp));
        console.log("sent data: " + JSON.stringify(tmp) + " to ip: " + ips[i]);
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

    //if the event is regDevice a new device will be added to the devices array
    //also sends the new devices to the webclient
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
        //put the received data into the database
        writeDataToDb(cmd.dname, cmd.data);
        //checks if a device has registered this data source and sends data to it
        checkAndSendData(cmd);
        //checks configurations and sends possible execute commands
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

    var flag = true;
    console.log("checkConfigurations called:" + JSON.stringify(cmd));

    //loops through all configurations to check for possible match
    for(var i = 0; i < configurations.length; i++){
        console.log("configuration: %s length: $d", configurations[i].cname, configurations.length);
        var config = configurations[i];
        for(var j = 0; j < configurations[i].conditions.length; j++) {
            console.log("conditions length: %d", configurations[i].conditions.length);
            var cnd = configurations[i].conditions[j];

            console.log(JSON.stringify(cnd));

            if(cnd.dname === "time")
                flag = checkTime(cnd);


            if(getTypeForDevice(cnd.dname) === "button"){

              console.log('cnd.value = ' + cnd.value + ' - cmd.data = ' + cmd.data);

              if(cnd.value === cmd.data){
                var val = true;
              }
              else{
                var val = false;
              }
              console.log('val: ' + val);
              console.log('config.logicalOperator: ' + config.logicalOperator);
              flag = applyLogicalOperator(val, flag, config.logicalOperator);

            }

            console.log("(config: %d, condition: %d): flag value: %s", i, j, flag);
        }


      //if the flag is true, the configurated action will be sent to the target device
      if(flag){

          if(getIpForDeviceName(config.dname) === -1){
            console.log('target device not found');
          }
          else{
            controllerWSS.sendToClient(getIpForDeviceName(config.dname), '{"event":"action","data":"' + config.action + '"}');
          }

      }

      flag = false;
    }

};
