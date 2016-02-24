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
 
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
        this.closeLatch.countDown();
    }
 
    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
    }
 
    @OnWebSocketMessage
    public void onMessage(String msg) {
        setChanged();
        notifyObservers(msg);
    }
    
    public void sendMessage(String payload){
        try {
            System.out.println("Method called");
            session.getRemote().sendString(payload);
        } catch (IOException ex) {
            Logger.getLogger(SimpleSocket.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
}
