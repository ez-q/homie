
var WebSocketServer = require('ws').Server
  , wss = new WebSocketServer({ port: 50555 });

wss.on('connection', function connection(ws) {
  console.log('connected: %s', ws._socket.remoteAddress);
  ws.on('message', function incoming(message) {
	console.log('received message: %s', message);
	var json = JSON.parse(message);
    wss.sendToClient(json.address,json.data);
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