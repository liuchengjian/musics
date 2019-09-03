package com.dqgb.lib_net.okHttp.response;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 业务逻辑层真正处理的地方，包括java层异常和业务层异常
 */
public interface DisposeDataListener {
    /**
     * 请求成功的回调
     * @param responseObj
     */
    void onSuccess(Object responseObj);

    /**
     * 请求失败的回调
     * @param responseObj
     */
    void onFailure(Object responseObj);
}
