/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.htl.delayedwebsocketclient;

import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

/**
 *
 * @author Viktor
 * The Websocket class that handles the communictaion between server and client. It is an observable class.
 */
@WebSocket
public class SimpleSocket extends Observable {

    private final CountDownLatch closeLatch;
 
    private Session session;
 
    public SimpleSocket() {
        this.closeLatch = new CountDownLatch(1);
    }
 
    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return this.closeLatch.await(duration, unit);
    }
    
    /**
     * Is called when the connection is closed. A message will be displayed.
     * @param statusCode
     * @param reason 
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown();
    }
    
    /**
     * Called when the client connects to the server and saves the session.
     * @param session 
     */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }
    
    /**
     * Called when the server sends a messge and notifies all observer.
     * @param msg 
     */
    @OnWebSocketMessage
    public void onMessage(String msg) {
        setChanged();
        notifyObservers(msg);
    }
    
    /**
     * Sends a simple string to the server.
     * @param payload 
     */
    public void sendMessage(String payload){
        try {
            System.out.println("Method called");
            session.getRemote().sendString(payload);
        } catch (IOException ex) {
            Logger.getLogger(SimpleSocket.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
