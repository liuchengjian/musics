package com.dqgb.lib_audio.envents;

import com.dqgb.lib_audio.CustomMediaPlayer;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 播放进度
 */
public class AudioProgressEvent {
    public CustomMediaPlayer.Status mStatus;
    public int progress;
    public int maxLength;

    public AudioProgressEvent(CustomMediaPlayer.Status status, int progress, int maxLength) {
        this.mStatus = status;
        this.progress = progress;
        this.maxLength = maxLength;
    }
}
