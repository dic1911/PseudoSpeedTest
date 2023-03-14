package moe.dic1911.test4speed;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {
    public static Context mContext = null;

    private static final String[] urls = {
            "https://speedtest.net",
            "https://www.speedtest.net/api/js/servers?engine=js&limit=10&https_functional=true",
            "https://c.speedtest.net/speedtest-servers-static.php",
            "https://c.speedtest.net/speedtest-servers.php"
    };

    public static void pokeSpeedTest() {
        Request r;
        for (String s : urls) {
            Log.d("HTTP_GET", s);
            r = new Request.Builder().header("Accept-Encoding", "gzip")
                    .header("Cache-Control", "no-cache")
                    .url(s).build();
            NetworkThread nt = new NetworkThread(s, r);
            nt.start();
            try {
                nt.join();
                Response resp = nt.getResponse();
                if (resp == null) {
                    Toast.makeText(mContext, mContext.getString(R.string.failed), Toast.LENGTH_LONG).show();
                    return;
                }
                if (resp.isSuccessful()) {
                    Log.d("HTTP_GET", String.valueOf(nt.getResponse().code()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
