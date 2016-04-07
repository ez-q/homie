package viktor.at.homieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/*
    Created by: Viktor Bär
 */
public class Connect extends AppCompatActivity {

    EditText etHost;
    Button btConnect;
    Button btExit;
    WSClient wsClient;

    /**
     * This is the startup activity and when it is created this method is called.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        //Retrieving the UI elements from the UI and setting default values
        etHost = (EditText) findViewById(R.id.etHostAddress);
        etHost.setText("ws://172.16.5.1:50556");
        btConnect = (Button) findViewById(R.id.btConnect);
        btExit = (Button) findViewById(R.id.btExit);
        wsClient = WSClient.getInstance();

        /*
            Checking if the client is already connected and if something was entered in the EditText for the URI.
            Afterwards an connection with the server is established and the DeviceListActivity is started.
         */
        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wsClient.start(etHost.getText().toString()) || wsClient.getmConnection().isConnected()){
                    Intent i = new Intent(getApplicationContext(),DeviceListActivity.class);
                    startActivity(i);
                }
            }
        });

        /*
            Ends the application on click.
         */
        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
