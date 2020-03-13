package com.ikonke.api.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    private static final TrustManager[] TRUST_MANAGERS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };
    private static SSLSocketFactory sslSocketFactory;

    static {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, TRUST_MANAGERS, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    public static OkHttpClient getBasicAuthClient(String user, String password) {
        return basicAuth(user, password).build();
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        return unsafe().build();
    }

    public static OkHttpClient getUnsafeBasicAuthClient(String user, String password) {
        return unsafe().addInterceptor(new BasicInterceptor(user, password)).build();
    }

    public static Map<Integer,String> get(OkHttpClient client, String url) {
        return get(client, url, null);
    }

    public static Map<Integer,String> get(OkHttpClient client, String url, Map<String, String> header) {
        Request.Builder builder = new Request.Builder();
        if (header != null) {
            Headers.Builder hb = new Headers.Builder();
            header.forEach(hb::add);
            builder.headers(hb.build());
        }
        Request request = builder.url(url).get().build();
        return execute(client, request);
    }

    public static Map<Integer,String> post(OkHttpClient client, String url, Object data) {
        return post(client, url, data, null);
    }

    public static Map<Integer,String> post(OkHttpClient client, String url, Object data, Map<String, String> header) {
        return request(client, url, "POST", data, header);
    }

    public static Map<Integer,String> postForm(OkHttpClient client, String url, Map<String, String> formData) {
        FormBody.Builder fb = new FormBody.Builder();
        formData.forEach(fb::add);
        FormBody formBody = fb.build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        return execute(client, request);
    }

    public static Map<Integer,String> delete(OkHttpClient client, String url, Object data, Map<String, String> header) {
        return request(client, url, "DELETE", data, header);
    }

    private static Map<Integer,String> request(OkHttpClient client, String url, String method, Object data, Map<String, String> header) {
        Headers headers = null;
        if (header != null) {
            Headers.Builder hb = new Headers.Builder();
            header.forEach(hb::add);
            headers = hb.build();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JsonUtil.toJson(data));
        Request.Builder rb = new Request.Builder().url(url);
        if (headers != null) {
            rb.headers(headers);
        }
        Request request;
        if ("DELETE".equals(method)) {
            request = rb.delete(requestBody).build();
        } else if ("PUT".equals(method)) {
            request = rb.put(requestBody).build();
        } else {
            request = rb.post(requestBody).build();
        }
        return execute(client, request);
    }

    private static OkHttpClient.Builder unsafe() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory);
        builder.hostnameVerifier((hostname, session) -> true);
        return builder;
    }

    private static OkHttpClient.Builder basicAuth(String user, String password) {
        return new OkHttpClient.Builder().addInterceptor(new BasicInterceptor(user, password));
    }

    private static Map<Integer,String> execute(OkHttpClient client, Request request) {
        try {
            Map<Integer,String> resp=new HashMap<>();
            Response response = client.newCall(request).execute();
            resp.put(response.code(),Objects.requireNonNull(response.body()).string());
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class BasicInterceptor implements Interceptor {
        private String credentials;
        public BasicInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }
        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }
    }

}