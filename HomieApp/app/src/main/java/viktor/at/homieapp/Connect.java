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

public class Connect extends AppCompatActivity {

    EditText etHost;
    Button btConnect;
    Button btExit;
    WSClient wsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        etHost = (EditText) findViewById(R.id.etHostAddress);
        etHost.setText("ws://10.0.2.2:50555");
        btConnect = (Button) findViewById(R.id.btConnect);
        btExit = (Button) findViewById(R.id.btExit);
        wsClient = WSClient.getInstance();

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wsClient.start(etHost.getText().toString()) || wsClient.getmConnection().isConnected()){
                    Intent i = new Intent(getApplicationContext(),DeviceListActivity.class);
                    startActivity(i);
                }
            }
        });

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
