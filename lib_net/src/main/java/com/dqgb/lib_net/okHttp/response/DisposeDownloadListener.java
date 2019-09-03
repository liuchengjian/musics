package com.dqgb.lib_net.okHttp.response;

import com.dqgb.lib_net.okHttp.response.DisposeDataListener;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 监听下载进度
 */
public interface DisposeDownloadListener extends DisposeDataListener {
    void onProgress(int progress);
}
