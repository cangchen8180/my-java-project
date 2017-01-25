package com.jimi.java.thirdUtilLib.OkHttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * HttpClient 的一个成熟的替代品！
 *
 * 语法很简洁，几乎每一行代码都是有用的。
 * 同时支持
 * 1、使用 GZIP 压缩减少传输的数据量;
 * 2、缓存，减少重复请求;
 * 3、SPDY；
 * 4、连接池；
 * 5、失败重试（如果你的服务有多个 IP 地址，如果第一次连接失败，OkHttp 将使备用地址）;
 *
 * @author jimi
 * @version 2017-01-19 20:43.
 */
public class UsingOkHttp {

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/repos/square/okhttp/issues")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String server = response.header("Server");
            System.out.println("server = " + server);

            String body = response.body().string();
            System.out.println("body = " + body);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
