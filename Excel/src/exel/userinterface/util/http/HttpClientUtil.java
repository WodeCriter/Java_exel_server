package exel.userinterface.util.http;

import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Consumer;

import static exel.userinterface.util.ScreenUtils.showAlert;

public class HttpClientUtil {

    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    private final static OkHttpClient HTTP_CLIENT =
            new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();
    private final static RequestBody EMPTY_BODY = RequestBody.create(null, new byte[0]);

    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";

    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    public static Callback getGenericCallback(ThrowingConsumer<Response> activateWhenOk,
                                              Runnable activateOnError) {
        return new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println(YELLOW + "Something went wrong: " + e.getMessage() + RESET);
                activateOnError.run();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() == 200)
                    activateWhenOk.accept(response);
                else
                {
                    activateOnError.run();
                    String message = response.body().string();
                    Platform.runLater(() -> showAlert("Something went wrong", message));
                    //System.out.println(YELLOW + "Something went wrong: " + response.body().string() + RESET);
                }

            }
        };
    }

    public static Callback getGenericCallback(ThrowingConsumer<Response> activateWhenOk) {
        return getGenericCallback(activateWhenOk, () -> {});
    }

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }


    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();
        runAsync(request, callback);
    }

    private static void runAsync(Request request, Callback callback) {
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(callback);
    }

    public static void runAsync(String finalUrl, ThrowingConsumer<Response> activateWhenOk){
        runAsync(finalUrl, getGenericCallback(activateWhenOk));
    }

    public static void runAsync(String finalUrl, HttpRequestType requestType, ThrowingConsumer<Response> activateWhenOk){
        Request.Builder builder = new Request.Builder().url(finalUrl);
        Request request = null;

        switch (requestType) {
            case POST:
                request = builder.post(EMPTY_BODY).build();
                break;
            case PUT:
                request = builder.put(EMPTY_BODY).build();
                break;
            case DELETE:
                request = builder.delete().build();
                break;
            case GET:
            default:
                request = builder.build();
                break;
        }
        runAsync(request, getGenericCallback(activateWhenOk));
    }

    public static void runAsync(Request request, ThrowingConsumer<Response> activateWhenOk){
        runAsync(request, getGenericCallback(activateWhenOk));
    }

    public static void runAsync(String URL, ThrowingConsumer<Response> activateWhenOk,
                                Runnable activateOnError){
        runAsync(URL, getGenericCallback(activateWhenOk, activateOnError));
    }

    public static void runAsync(Request request, ThrowingConsumer<Response> activateWhenOk,
                                Runnable activateOnError){
        runAsync(request, getGenericCallback(activateWhenOk, activateOnError));
    }

    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}