  #include <ESP8266WiFi.h>
#include <WebSocketClient.h>




const char* ssid     = "Liwest_af772c"; //name of your WiFi network
const char* password = "scheinecker_w"; //password of your WiFi network, leave blank if no passwort

//path is the directory for your WebSocket, in this case (a WebSocketServer with Java EE)
//the Endpoint ('/') and the project name ('WSDemoESP8266') is put front of it -- MIND THE FRONTSLASHES!!
char path[] = "/";

//host is the ip of the host computer ('172.16.6.110') with the port ('8080'), on which 
//the WebSocketServer runs, concatinated after ':' -- NO FRONTSLASHES HERE!!
char host[] = "192.168.0.12:50555";

WebSocketClient webSocketClient;

// Use WiFiClient class to create TCP connections
WiFiClient client;



void setup() {
  Serial.begin(9600);
  delay(10);
  pinMode(BUILTIN_LED, OUTPUT);
  pinMode(2, OUTPUT);

  digitalWrite(BUILTIN_LED, HIGH);
  digitalWrite(2, LOW);
  // We start by connecting to a WiFi network

  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.println("");
  Serial.println("WiFi connected");  
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  delay(5000);
  

  //establishing a TCP-Connection with the host.
  //args:
  //      1. - ip address of the host (same as above)
  //      2. - port of the host (same as above)
  if (client.connect("192.168.0.12", 50555)) {
    Serial.println("Connected");
  } else {
    Serial.println("Connection failed.");
    while(1) {
      // Hang on failure
    }
  }

  // Handshake with the server
  webSocketClient.path = path;
  webSocketClient.host = host;
  
  
  if (webSocketClient.handshake(client)) {
    Serial.println("Handshake successful");
    
    digitalWrite(BUILTIN_LED, LOW);
  } else {
    Serial.println("Handshake failed.");
    while(1) {
      // Hang on failure
    }  
  }

}


void loop() {
  String data;
  String toSend;
  


  
  //run as long as the client is connected
  
  if (client.connected()) {
   

    //check if data is received, if yes: display it on the serial monitor
    webSocketClient.getData(data);
    /*  Serial.print("Received data: ");
      Serial.println(data);
    }*/

    if(data.length() > 0){
      Serial.println("read data: " + data);
      digitalWrite(2, HIGH);
    }
    
    // capture the value of analog 1
    /*pinMode(2, INPUT);
    data = String(digitalRead(2));*/

    
  
    /*if(data == "1"){
      //send data to the WebSocketServer, in this case the value of analog pin 1
      //sprintf(ip, "%d.%d.%d.%d", ipno[0], ipno[1], ipno[2], ipno[3]);
      toSend = "{\"from\":\"ESP1\",\"data\":\"BUTTON_PRESSED\"}";
      webSocketClient.sendData(toSend);
      
      
    }*/
    
  } else {
    Serial.println("Client disconnected.");
    while (1) {
      // Hang on disconnect.
    }
  }
  
  // wait to fully let the client disconnect
  //delay(1000);
} 
