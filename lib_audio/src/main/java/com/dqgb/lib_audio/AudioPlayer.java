package com.dqgb.lib_audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.dqgb.lib_audio.envents.AudioCompleteEvent;
import com.dqgb.lib_audio.envents.AudioErrorEvent;
import com.dqgb.lib_audio.envents.AudioLoadEvent;
import com.dqgb.lib_audio.envents.AudioPauseEvent;
import com.dqgb.lib_audio.envents.AudioReleaseEvent;
import com.dqgb.lib_audio.envents.AudioStartEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/9/3.
 * 播放器事件源
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener
        , MediaPlayer.OnBufferingUpdateListener
        , MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;
    private static final int LOAD_ERROR = 1;//加载出错
    private static final int PALY_ERROR = 2;//播放出错

    //真正负责播放的核心MediaPlayer子类
    private CustomMediaPlayer mMediaPlayer;
    //增加后台的保活能力
    private WifiManager.WifiLock mWifiLock;
    //音频焦点监听器
    private AudioFocusManager mAudioFocusManager;
    private boolean isPauseByFocusLossTransient = false;//争取音频失败

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    break;

            }
        }
    };

    public AudioPlayer() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mWifiLock = ((WifiManager) AudioHelper.
                getContext().
                getApplicationContext().
                getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    /**
     * 对外提供的加载
     *
     * @param audioBean
     */

    public void load(AudioBean audioBean) {
        try {
            //正常加载逻辑
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //对外发送load事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));

        } catch (Exception e) {
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent(LOAD_ERROR));
        }
    }

    /**
     * 开始播放
     */
    private void start() {
        if (!mAudioFocusManager.requestAudioFocus()) {
            //其他app使用播放焦点
            Log.e(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        mWifiLock.acquire();
        //对外发送start事件
        EventBus.getDefault().post(new AudioStartEvent());

    }

    /**
     * 对外提供暂停
     */
    public void pause() {
        //对外发送start事件
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            //释放WifiLock
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            //释放音频焦点
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
        }
        mWifiLock.acquire();
        //对外发送pause事件
        EventBus.getDefault().post(new AudioPauseEvent());
    }

    /**
     * 对外提供恢复
     */
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    /**
     * 清空播放资源
     */
    public void release() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.release();
        mMediaPlayer = null;
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        //释放音频焦点
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        mWifiLock = null;
        mAudioFocusManager = null;
        //发送release事件
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**
     * 获取播放器当前状态
     *
     * @return
     */
    public CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getState();
        }
        return CustomMediaPlayer.Status.STOPPED;
    }


    /**
     * 设置音量
     *
     * @param left  左声道
     * @param right 右声道
     */
    private void setVolumn(float left, float right) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(left, right);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //缓存进度的回调
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完毕的回调
        EventBus.getDefault().post(new AudioCompleteEvent());

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //true,自行处理错误，播放出错
        //对外发送error事件
        EventBus.getDefault().post(new AudioErrorEvent(PALY_ERROR));
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //准备完毕
        start();
    }


    @Override
    public void audioFocusGrant() {
        //再次获得音频焦点
        setVolumn(1.0f, 1.0f);
        if (isPauseByFocusLossTransient) {
            resume();
        }
        isPauseByFocusLossTransient = false;

    }


    @Override
    public void audioFocusLoss() {
        //永久失去焦点
        pause();
    }

    @Override
    public void audioFocusLossTransient() {
        //短暂后失去焦点
        pause();
        isPauseByFocusLossTransient = true;
    }

    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点，通知，短信
        setVolumn(0.5f, 0.5f);
    }
}
