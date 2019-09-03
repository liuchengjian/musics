package com.dqgb.lib_audio.envents;

import com.dqgb.lib_audio.CustomMediaPlayer;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 */
public class AudioErrorEvent {
    //出错的编码，1:加载出错，2:播放出错
    public int Code;

    public AudioErrorEvent(int Code) {
        this.Code = Code;
    }
}
