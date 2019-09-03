package com.dqgb.lib_audio.envents;

import com.dqgb.lib_audio.AudioBean;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 */
public class AudioLoadEvent {
    public AudioBean mAudioBean;

    public AudioLoadEvent(AudioBean audioBean) {
        this.mAudioBean = audioBean;
    }
}
