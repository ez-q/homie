package at.htl.delayedwebsocketclient;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * The main logic behind the fxml project.
 * @author Viktor
 */

public class FXMLController implements Initializable, Observer {

    @FXML
    private TextField tfConnection;
    @FXML
    private Button btConnection;
    @FXML
    private TextArea taInput;
    @FXML
    private TextArea taOutput;
    @FXML
    private Button btSend;
    @FXML
    private CheckBox cbIsDelayed;
    @FXML
    private TextField tfDelay;

    SimpleSocket socket = null;
    
    /**
     * This method is called when the program is started. It sets the onAction listener for the button in which the connection to the server
     * is attempted.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btConnection.setOnAction((event) -> {
            try {
                WebSocketClient client = new WebSocketClient();
                socket = new SimpleSocket();
                socket.addObserver(this);
                client.start();
                URI uri = new URI(tfConnection.getText());
                ClientUpgradeRequest request = new ClientUpgradeRequest();
                client.connect(socket, uri, request);
                taOutput.appendText("attempting to connect to: " + tfConnection.getText() + "\n");
            } catch (Exception ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        /*
            The specified messages in the text area are put in a string array.
            Afterwards the messages are queued and sent to the server with a specified interval.
        */
        btSend.setOnAction((event) -> {

            String[] stringQueue = taInput.getText().split("\n");

            if (cbIsDelayed.isSelected() && !tfDelay.getText().isEmpty()) {
                Timeline timeline = new Timeline(new KeyFrame(
                                Duration.valueOf(tfDelay.getText()), new EventHandler<ActionEvent>() {
                                    
                                int refI = 0;

                                public void handle(ActionEvent event1) {
                                    socket.sendMessage(stringQueue[refI]);
                                    refI++;
                                }
                }));
                timeline.setCycleCount(stringQueue.length);
                timeline.play();
            } else{
                for (int i = 0; i < stringQueue.length; i++)
                    socket.sendMessage(stringQueue[i]);
            }
            
        });

    }
    
    /*
        Called when the Client class gets a message and appends it to the text area.
    */
    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            taOutput.appendText((String) arg + "\n");
        });
    }
}
