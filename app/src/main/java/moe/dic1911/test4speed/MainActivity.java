package moe.dic1911.test4speed;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button btn_run, btn_svc;
    private NetworkService svc;
    private static boolean svcStarted;
    private static Intent svcIntent;

    @Override
    protected void onResume() {
        super.onResume();
        svcStarted = isServiceRunning(NetworkService.class);
        btn_svc.setText(svcStarted ? getString(R.string.stop_svc) : getString(R.string.start_svc));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        svcStarted = isServiceRunning(NetworkService.class);
        btn_run = findViewById(R.id.btn_run);
        btn_svc = findViewById(R.id.btn_svc);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtils.pokeSpeedTest();
            }
        });

        if (svcIntent == null) svcIntent = new Intent(getApplicationContext(), NetworkService.class);
        //btn_svc.setEnabled(!svcStarted);
        btn_svc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!svcStarted) {
                    getApplicationContext().startService(svcIntent);
                    //btn_svc.setEnabled(!svcStarted);
                } else {
                    getApplicationContext().stopService(svcIntent);
                }
                svcStarted = !svcStarted;
                btn_svc.setText(svcStarted ? getString(R.string.stop_svc) : getString(R.string.start_svc));
            }
        });
    }

    // credit: Peter Mortensen and geekQ from StackOverflow
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
