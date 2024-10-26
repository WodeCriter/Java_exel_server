package exel.userinterface.util.http;

import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static Callback getGenericCallback(Consumer<Response> activateWhenOk) {

        return new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200)
                    activateWhenOk.accept(response);
                else
                    System.out.println("Something went wrong: " + response.body());
            }
        };
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public static void runAsync(String url, String queryName, String queryValue, Callback callback) {
        String finalURL = HttpUrl
                .parse(url)
                .newBuilder()
                .addQueryParameter(queryName, queryValue)
                .build()
                .toString();
        runAsync(finalURL, callback);
    }

    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        runAsync(request, callback);
    }

    public static void runAsync(Request request, Callback callback) {
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsync(String url, String queryName, String queryValue, Consumer<Response> activateWhenOk){
        runAsync(url, queryName, queryValue, getGenericCallback(activateWhenOk));
    }

    public static void runAsync(String finalUrl, Consumer<Response> activateWhenOk){
        runAsync(finalUrl, getGenericCallback(activateWhenOk));
    }

    public static void runAsync(Request request, Consumer<Response> activateWhenOk){
        runAsync(request, getGenericCallback(activateWhenOk));
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}