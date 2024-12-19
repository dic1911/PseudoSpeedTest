package moe.dic1911.test4speed;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkThread extends Thread implements Runnable {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:133.0) Gecko/20100101 Firefox/133.0";

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
                .addNetworkInterceptor(new UserAgentInterceptor(USER_AGENT))
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

    public static class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
