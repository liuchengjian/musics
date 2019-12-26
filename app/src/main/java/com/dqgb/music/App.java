package com.dqgb.music;

import android.app.Application;

import com.dqgb.lib_audio.AudioHelper;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/10/28.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //音频SDK初始化
        AudioHelper.init(this);
    }
}
