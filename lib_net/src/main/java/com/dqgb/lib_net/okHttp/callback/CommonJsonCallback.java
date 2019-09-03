package com.dqgb.lib_net.okHttp.callback;


import android.os.Handler;
import android.os.Looper;

import com.dqgb.lib_net.okHttp.utils.OkHttpException;
import com.dqgb.lib_net.okHttp.response.DisposeDataHandle;
import com.dqgb.lib_net.okHttp.response.DisposeDataListener;
import com.dqgb.lib_net.okHttp.response.ResponseEntityToModule;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 */
public class CommonJsonCallback implements BaseCallback {
    protected final String EMPTY_MSG = "";
    protected final int NETWORK_ERROR = -1;
    protected final int JSON_ERROR = -2;
    protected final int OTHER_MSG = -3;
    private Class<?> mClass;
    private DisposeDataListener mListener;
    private Handler mHander;

    public CommonJsonCallback(DisposeDataHandle handle) {
        mListener = handle.mListener;
        mClass = handle.mClass;
        mHander = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull final IOException e) {
        mHander.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });
    }

    /**
     * 响应成功
     *
     * @param call
     * @param response
     * @throws IOException
     */
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final String result = response.body().toString();
        mHander.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(String result) {
        if (result == null || result.trim().equals("")) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
        }
        try {
            if (mClass == null) {
                //拿到的对象为空，不想让框架处理，直接返回
                mListener.onSuccess(result);
            } else {
                //网络框架，解析，可以Gson，fastjson进行解析
                Object obj = ResponseEntityToModule.parseJsonToModule(result, mClass);
                if (obj != null) {
                    //解析成功
                    mListener.onSuccess(obj);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(JSON_ERROR, e));
        }
    }

}
