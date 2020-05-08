package moe.dic1911.test4speed;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import okhttp3.Request;

public class NetworkService extends Service {

    private static final int interval = 90 * 1000; // 1.5 min
    private static final String CHANNEL_ID = "default";
    private static android.app.Notification noti;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Default Notification Ch.",
                    NotificationManager.IMPORTANCE_MIN);

            if (((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).getNotificationChannels().size() == 0) {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();


        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        noti = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setOngoing(true)
                .setContentTitle(getString(R.string.running_svc))
                .setContentText(getString(R.string.running))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_foreground))
                .setContentIntent(pi)
                .setDefaults(Notification.DEFAULT_SOUND).setVibrate(new long[]{0L})
                .setVibrate(null)
                .setChannelId(CHANNEL_ID)
                .build();

        //Start service, but different code for different android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(1, noti);
        } else {
            startService(new Intent(this, NetworkService.class));
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO: monitor activity + timer?
                while(true) {
                    try {
                        NetworkUtils.pokeSpeedTest();
                        Thread.currentThread().sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                stopSelf();
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }
}
