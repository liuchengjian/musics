package com.dqgb.lib_net.okHttp.utils;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 自定义异常类,返回ecode,emsg到业务层
 */
public class OkHttpException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * 报错码
     */
    private int ecode;

    /**
     * 报错提示信息
     */
    private Object emsg;

    public OkHttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}
