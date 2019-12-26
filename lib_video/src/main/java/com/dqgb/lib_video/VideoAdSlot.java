package com.dqgb.lib_video;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Author by lchj,
 * Email 627107345 @qq.com, Date on 2019/10/28.
 * 视频业务逻辑层
 */
public class VideoAdSlot implements CustomVideoView.ADVideoPlayerListener {
    private Context mContext;
    /**
     * UI
     */
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    /**
     * Data
     */
//    @Autowired(name = "/audio/audio_service")
//    protected AudioService mAudioService;
    private String mXAdInstance;
    private SDKSlotListener mSlotListener;

    public VideoAdSlot(String adInstance, SDKSlotListener slotLitener) {
//        ARouter.getInstance().inject(this);
        mXAdInstance = adInstance;
        mSlotListener = slotLitener;
        mParentView = slotLitener.getAdParent();
        mContext = mParentView.getContext();
        initVideoView();
    }

    /**
     * 初始化VideoView
     */
    private void initVideoView() {
        mVideoView = new CustomVideoView(mContext);
        if (mXAdInstance != null) {
            mVideoView.setDataSource(mXAdInstance);
            mVideoView.setListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);//将视频添加到播放容器中
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mXAdInstance = null;
    }

    @Override
    public void onClickFullScreenBtn() {
        //切换大小屏
        mParentView.removeView(mVideoView);//从容器中移除
        VideoFullDialog dialog =
                new VideoFullDialog(mContext, mVideoView, mXAdInstance, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //回到小屏继续处理
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                bigPlayComplete();
            }
        });
//        dialog.setViewBundle(bundle); //为Dialog设置播放器数据Bundle对象
        dialog.setSlotListener(mSlotListener);
        dialog.show();
        //全屏暂停音乐播放
//        mAudioService.pauseAudio();
    }

    /**
     * 回到小屏处理
     *
     * @param position
     */
    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndResume(position);
        //小屏恢复音乐播放
//        mAudioService.resumeAudio();
    }

    /**
     * 全屏返回小屏继续播放事件
     */
    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        mVideoView.setTranslationY(0); //防止动画导致偏离父容器
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setListener(this);
        mVideoView.seekAndPause(0);
    }

    @Override
    public void onClickVideo() {

    }

    @Override
    public void onClickBackBtn() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void onAdVideoLoadSuccess() {

    }

    @Override
    public void onAdVideoLoadFailed() {

    }

    @Override
    public void onAdVideoLoadComplete() {

    }

    //传递消息到appcontext层
    public interface SDKSlotListener {

        ViewGroup getAdParent();

        void onVideoLoadSuccess();

        void onVideoFailed();

        void onVideoComplete();
    }
}
