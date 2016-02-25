package viktor.at.homieapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Viktor on 25.02.2016.
 */
public class SplashActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    sleep(5000);  //Delay of 10 seconds
                } catch (Exception e) {

                } finally {

                    Intent i = new Intent(getApplicationContext(),
                            Connect.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }
}
