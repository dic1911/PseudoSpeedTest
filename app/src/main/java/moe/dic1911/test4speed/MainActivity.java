package moe.dic1911.test4speed;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_run = findViewById(R.id.btn_run);
        btn_svc = findViewById(R.id.btn_svc);
        btn_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetworkUtils.pokeSpeedTest();
            }
        });

        // not working rn
        btn_svc.setEnabled(false);
        btn_svc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (svc == null) {
                    getSystemService(Context.ACTIVITY_SERVICE);
                    getApplicationContext().startService(new Intent(getApplicationContext(), NetworkService.class));
                }
            }
        });
    }
}
