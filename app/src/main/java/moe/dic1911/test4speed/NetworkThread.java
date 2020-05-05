package moe.dic1911.test4speed;

import android.util.Log;

import java.io.IOException;

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
        this.hc = new OkHttpClient();
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
            Log.d("HTTP_GET", String.valueOf(this.res.code()));
            if (!res.isSuccessful()) {
                Log.d("HTTP_GET", "failed to get response");
            }
        } catch (IOException e) {
            Log.d("HTTP_GET", "failed to get response");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        exec();
    }
}
