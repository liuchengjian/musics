package com.dqgb.lib_net.okHttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/2.
 * 对外提供post/get/下载网络请求
 */
public class CommonRequest {
    /**
     * 对外创建post请求对象
     *
     * @param url    请求地址
     * @param params 自定义请求体
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params) {
        return createPostRequest(url, params, null);
    }

    /**
     * 对外创建post请求对象
     *
     * @param url     请求地址
     * @param params  自定义请求体
     * @param headers 自定义请求头
     * @return
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder mFormBodyBuilder = new FormBody.Builder();//创建请求体FormBody
        Headers.Builder mHeaderBuilder = new Headers.Builder();//创建请求头Builder
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //参数遍历
                mFormBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                //请求头遍历
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(url)
                .headers(mHeaderBuilder.build())
                .post(mFormBodyBuilder.build())
                .build();
        return request;
    }

    /**
     * 对外创建get请求对象
     *
     * @param url    请求地址
     * @param params 自定义请求体
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params) {
        return createGetRequest(url, params, null);
    }

    /**
     * 对外创建get请求对象
     *
     * @param url     请求地址
     * @param params  自定义请求体
     * @param headers 自定义请求头
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers) {
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        Headers.Builder mHeaderBuilder = new Headers.Builder();//创建请求头Builder
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                //参数遍历
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                //请求头遍历
                mHeaderBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .headers(mHeaderBuilder.build())
                .build();
        return request;
    }

    public static final MediaType File_TYPE = MediaType.parse("application/octet-stream");

    public static Request createMultiPostRequest(String url, RequestParams params, RequestParams headers) {
        MultipartBody.Builder mRequestBody = new MultipartBody.Builder();
        mRequestBody.setType(MultipartBody.FORM);//默认表单提交
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                //参数遍历
                if (entry.getValue() instanceof File) {
                    mRequestBody.addPart(Headers.of("Content-DisPosition", "form-data;name=\"" + entry.getKey() + "\"")
                            , RequestBody.create(File_TYPE, (File) entry.getValue()));
                } else if (entry.getValue() instanceof String) {
                    mRequestBody.addPart(Headers.of("Content-DisPosition", "form-data;name=\"" + entry.getKey() + "\"")
                            , RequestBody.create(null, (String) entry.getValue()));
                }
            }
        }
        return null;
    }
}
