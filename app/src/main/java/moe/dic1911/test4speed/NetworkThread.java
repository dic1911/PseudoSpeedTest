package moe.dic1911.test4speed;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkThread extends Thread implements Runnable {

    private OkHttpClient hc;
    private String url;
    private Request req;
    private Response res;

    NetworkThread(String url, Request req) {
        this.url = url;
        this.req = req;
        this.res = null;
        this.hc = new OkHttpClient.Builder()
                .followRedirects(true)
                .retryOnConnectionFailure(true)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    void setRequest (Request req) {
        this.req = req;
    }

    Response getResponse() {
        return res;
    }

    void exec() {
        try {
            this.res = hc.newCall(req).execute();
            Log.d("HTTP_GET", String.format("%s: %s", req.url(), res.code()));
            if (res == null || !res.isSuccessful()) {
                NetworkUtils.hasError = true;
                Log.d("HTTP_GET", String.format("failed to get response: %s", req.url()));
            }
        } catch (IOException e) {
            NetworkUtils.hasError = true;
            Log.d("HTTP_GET", String.format("failed to get response: %s", req.url()));
            e.printStackTrace();
        } finally {
            if (res != null) {
                res.close();
            }
        }
        NetworkUtils.guard.countDown();
    }

    @Override
    public void run() {
        exec();
    }
}
