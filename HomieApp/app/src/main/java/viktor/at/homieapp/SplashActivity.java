package viktor.at.homieapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/*
    Created by: Viktor Bär
    This is a splash activity. It is the startup activity and shows a screen with the logo on the start before the
    connection activity is called. It was decided to not use it because it had no real use but look good.
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
                    sleep(5000);
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
