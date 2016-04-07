#include <ESP8266WiFi.h>
#include <WebSocketClient.h>
#include <ArduinoJson.h>





const char* ssid     = "htlleonding-cisco-edv6"; //name of your WiFi network
const char* password = ""; //password of your WiFi network, leave blank if no passwort

//path is the directory for your WebSocket, in this case (a WebSocketServer with Java EE)
//the Endpoint ('/') and the project name ('WSDemoESP8266') is put front of it -- MIND THE FRONTSLASHES!!
char path[] = "/";

//host is the ip of the host computer ('172.16.6.110') with the port ('8080'), on which 
//the WebSocketServer runs, concatinated after ':' -- NO FRONTSLASHES HERE!!
char host[] = "172.16.5.1:50555";

WebSocketClient webSocketClient;

// Use WiFiClient class to create TCP connections
WiFiClient client;



void setup() {
  Serial.begin(9600);
  delay(10);
  pinMode(BUILTIN_LED, OUTPUT);

  digitalWrite(BUILTIN_LED, HIGH);
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
  if (client.connect("172.16.5.1", 50555)) {
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

  String regString;
  regString = "{\"event\":\"regDevice\",\"category\":\"sensor\",\"type\":\"button\",\"dname\":\"ESPButton\",\"values\":\"[true,false]\"}";
  webSocketClient.sendData(regString);

}


void captureAndSendButtonData(){
    pinMode(2, INPUT);
    String data = String(digitalRead(2));
    String toSend;
    if(data == "1"){
      toSend = "{\"dname\":\"ESPButton\",\"data\":true}";
    }
    else{
      toSend = "{\"dname\":\"ESPButton\",\"data\":false}";
    }
    webSocketClient.sendData(toSend);
}

void loop() {
  String data;

  StaticJsonBuffer<200> jsonBuffer;
  
  //run as long as the client is connected
  if (client.connected()) {
    

    //check if data is received, if yes: display it on the serial monitor
    webSocketClient.getData(data);
    Serial.print("Received data: ");
    Serial.println(data);

    JsonObject& root = jsonBuffer.parseObject(data);

    if(!root.success()){
      Serial.println("parseObject() failed");
    }
    else{
      String event = root["event"];
      String data = root["data"];

      if(event == "sendData"){
        captureAndSendButtonData();
      }
    }

    
    
    String data = String(digitalRead(2));
    captureAndSendButtonData();
    
      
      
    
    
  } else {
    Serial.println("Client disconnected.");
    while (1) {
      // Hang on disconnect.
    }
  }
  
  // wait to fully let the client disconnect
  delay(1000);
} 
