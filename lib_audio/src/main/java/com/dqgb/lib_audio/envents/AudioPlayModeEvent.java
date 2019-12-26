package com.dqgb.lib_audio.envents;

import com.dqgb.lib_audio.AudioController;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 播放模式切换事件
 */
public class AudioPlayModeEvent {
    public AudioController.PlayMode mPlayMode;

    public AudioPlayModeEvent(AudioController.PlayMode playMode) {
        this.mPlayMode = playMode;
    }
}
