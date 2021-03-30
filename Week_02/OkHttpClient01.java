
import okhttp3.*;

import java.io.IOException;

/**
 * @author kanea
 */
public class OkHttpClient01 {
    public static void main(String[] args) {
        sendRequest("http://localhost:8081");
    }

    /**
     * 使用OkHttp发起网络请求
     *
     * @param url 请求地址
     */
    private static void sendRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        System.out.println(body.string());
                    }
                }
            }
        });
    }
}
