package moe.dic1911.test4speed;

import android.widget.Toast;

import java.util.concurrent.CountDownLatch;

import okhttp3.Request;

public class NetworkUtils {
    public static MainActivity mContext = null;
    public static Boolean hasError = false;
    public static CountDownLatch guard = null;

    private static final String[] urls = {
            "https://speedtest.net",
            "https://www.speedtest.net/api/js/servers?engine=js&limit=10&https_functional=true",
            "https://c.speedtest.net/speedtest-servers-static.php",
            "https://c.speedtest.net/speedtest-servers.php"
    };

    public static void pokeSpeedTest() {
        Request r;
        hasError = false;
        guard = new CountDownLatch(urls.length);

        for (String s : urls) {
            r = new Request.Builder().header("Accept-Encoding", "gzip")
                    .header("Cache-Control", "no-cache")
                    .url(s).build();
            new NetworkThread(s, r).start();
        }

        new Thread(() -> {
            try {
                while (guard.getCount() > 0) {
                    guard.await();
                }

                if (hasError) {
                    mContext.runOnUiThread(() -> {
                        Toast.makeText(mContext, mContext.getString(R.string.failed), Toast.LENGTH_LONG).show();
                    });
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
