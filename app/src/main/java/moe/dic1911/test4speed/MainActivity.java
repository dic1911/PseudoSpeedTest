package moe.dic1911.test4speed;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button btn_run, btn_svc;
    private NetworkService svc;
    private Context mContext;
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
        mContext = getApplicationContext();
        NetworkUtils.mContext = this;
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, R.string.running, Toast.LENGTH_SHORT).show();
                NetworkUtils.pokeSpeedTest();
                Toast.makeText(mContext, R.string.done, Toast.LENGTH_SHORT).show();
            }
        });

        if (svcIntent == null) svcIntent = new Intent(mContext, NetworkService.class);
        btn_svc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!svcStarted) {
                    getApplicationContext().startService(svcIntent);
                    Toast.makeText(mContext, R.string.running_svc, Toast.LENGTH_SHORT).show();
                } else {
                    getApplicationContext().stopService(svcIntent);
                    Toast.makeText(mContext, R.string.stopped_svc, Toast.LENGTH_SHORT).show();
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
