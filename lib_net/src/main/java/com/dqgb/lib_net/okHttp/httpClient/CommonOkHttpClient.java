package com.dqgb.lib_net.okHttp.httpClient;

import com.dqgb.lib_net.okHttp.callback.CommonFileCallback;
import com.dqgb.lib_net.okHttp.callback.CommonJsonCallback;
import com.dqgb.lib_net.okHttp.response.DisposeDataHandle;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 */
public class CommonOkHttpClient {
    private static final int TIME_OUT = 30;//响应时间 单位秒
    private static OkHttpClient mOkHttpClient;

    //完成对okHttpClient的初始化
    static {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;//true,对域名默认都是信任
            }
        });
        /**
         * 添加公用请求头
         */
        okHttpClientBuilder.addInterceptor(new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("User-Agent", "Imooc-Mpbile")
                        .build();
                return chain.proceed(request);
            }
        });
        okHttpClientBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);//连接超时时间
        okHttpClientBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);//读超时间
        okHttpClientBuilder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);//写超时间
        okHttpClientBuilder.followRedirects(true);//允许重定向
        mOkHttpClient = okHttpClientBuilder.build();
    }

    /**
     * get请求和Post请求
     *
     * @return
     */
    public static Call getAndPost(Request request, DisposeDataHandle handle) {
        //如队列
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handle));
        return call;
    }

    /**
     * 文件下载请求
     *
     * @return
     */
    public static Call downLoadFile(Request request, DisposeDataHandle handle) {
        //如队列
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new CommonFileCallback(handle));
        return call;
    }
}
