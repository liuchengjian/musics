package com.dqgb.lib_net.okHttp.callback;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.dqgb.lib_net.okHttp.utils.OkHttpException;
import com.dqgb.lib_net.okHttp.response.DisposeDataHandle;
import com.dqgb.lib_net.okHttp.response.DisposeDownloadListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 文件下载的Callback
 */
public class CommonFileCallback implements BaseCallback {
    protected final String EMPTY_MSG = "";
    protected final int NETWORK_ERROR = -1;
    protected final int IO_ERROR = -2;
    //处理进度的一个时间类型
    private static final int PROGRESS_MESSAGE = 0x01;
    private DisposeDownloadListener mListener;
    private Handler mHander;
    private String mFilePath;
    private int mProgress;//当前进度

    public CommonFileCallback(DisposeDataHandle handle) {
        this.mListener = (DisposeDownloadListener) handle.mListener;
        this.mFilePath = handle.mSource;
        this.mHander = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListener.onProgress((int) msg.obj);
                        break;
                }
            }
        };
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
        final File file = handleResponse(response);
        mHander.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListener.onSuccess(file);
                } else {
                    mListener.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    private File handleResponse(Response response) {
        if (response == null) {
            return null;
        }
        InputStream inputStream = null;//输入流
        File file = null;
        FileOutputStream fos = null;//输出流
        byte[] buffer = new byte[2048];//缓存
        double currentLength = 0;//读写的长度
        double sumLength;//总长
        int length;
        try {
            //判断文件是否存在，不存在，怎创建
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLength = response.body().contentLength();
            //循环读取
            while ((length = inputStream.read(buffer)) != -1) {
                //没有读完，开始写
                fos.write(buffer, 0, buffer.length);
                currentLength += length;
                mProgress = (int) (currentLength / sumLength * 100);
                mHander.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            //读写完毕
            fos.flush();
        } catch (Exception e) {
            file = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                file = null;
            }
        }
        return file;
    }

    /**
     * 判断文件是否存在，不存在，怎创建
     *
     * @param localFilePath
     */
    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
