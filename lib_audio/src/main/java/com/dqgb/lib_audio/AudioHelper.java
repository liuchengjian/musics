package com.dqgb.lib_audio;

import android.content.Context;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 初始化Audio 播放器
 */
public class AudioHelper {

    //SDK全局Context, 供子模块用
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        //初始化本地数据库
//        GreenDaoHelper.initDatabase();
    }

    //外部启动MusicService方法
//    public static void startMusicService(ArrayList<AudioBean> audios) {
//        MusicService.startMusicService(audios);
//    }
//
//    public static void addAudio(Activity activity, AudioBean bean) {
//        AudioController.getInstance().addAudio(bean);
//        MusicPlayerActivity.start(activity);
//    }
//
//    public static void pauseAudio() {
//        AudioController.getInstance().pause();
//    }
//
//    public static void resumeAudio() {
//        AudioController.getInstance().resume();
//    }

    public static Context getContext() {
        return mContext;
    }
}
