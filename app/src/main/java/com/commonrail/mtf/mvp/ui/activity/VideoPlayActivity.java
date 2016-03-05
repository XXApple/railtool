package com.commonrail.mtf.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.ui.base.BaseActivity;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.SysUtils;
import com.commonrail.mtf.util.common.ViewUtils;
import com.commonrail.rtplayer.RtPlayer;
import com.commonrail.rtplayer.listener.RtPlayerListener;
import com.commonrail.rtplayer.view.RtVideoView;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/13 下午8:41
 * 修改人：wengyiming
 * 修改时间：16/1/13 下午8:41
 * 修改备注：
 */
public class VideoPlayActivity extends BaseActivity {
    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    break;
                default:
                    break;
            }
            return false;
        }
    });
    @Bind(R.id.fl_loading)
    FrameLayout mFlLoading;
    @Bind(R.id.fl_download_rate)
    FrameLayout mFlDownloadRate;
    @Bind(R.id.tv_download_rate)
    TextView mTvDownloadRate;
    @Bind(R.id.ll_brightness)
    LinearLayout mLlBrightness;
    @Bind(R.id.ll_volume)
    LinearLayout mLlVolume;
    @Bind(R.id.tv_brightness)
    TextView mTvBrightness;
    @Bind(R.id.tv_volume)
    TextView mTvVolume;
    @Bind(R.id.video_hd)
    ImageView mIvHD;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.video_title_bar)
    LinearLayout mTitleBar;
    @Bind(R.id.iv_play)
    ImageView mIvPlay;
    @Bind(R.id.tv_rate)
    TextView mTvRate;
    @Bind(R.id.sb_rate)
    SeekBar mPbRate;
    @Bind(R.id.video_controller_bar)
    LinearLayout mControllerBar;
    @Bind(R.id.rt_video_view)
    RtVideoView mOkVideoView;
    private AudioManager mAudioManager;
    private String mVideoUrl;
    private String mSourceId;
    private String mSourceType;
    private String mSourceTitle;
    private boolean isSeekBarThreadRun = false;
    private boolean isPlayOver = false;
    private Uri mUri;
    private GestureDetector mGestureDetector;
    private long currentPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        View decorView = getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ViewUtils.toggleHideyBar(decorView);
        super.onCreate(savedInstanceState);
        initView();
        danmuku();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_video_play;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOkVideoView != null) {
            mOkVideoView.onDestroy();
        }
        isPlayOver = true;

    }

    public void initView() {
        initDate();
        if (mSourceTitle != null) {
            mTvTitle.setText(mSourceTitle);
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this, new CustomTouchListener());
        currentPosition = getIntent().getLongExtra("currentPosition", 0);
        if (currentPosition > 10000) {
            currentPosition = currentPosition - 2000;
        }
        if (TextUtils.isEmpty(mSourceType)) {
            GlobalUtils.showToastShort(this, "读取视频源出错");
        }

    }

    private void danmuku() {

        mOkVideoView.addListener(new RtPlayerListener() {
            @Override
            public void onStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == RtPlayer.STATE_ENDED) {
                    isPlayOver = true;
                    mPbRate.setMax((int) mOkVideoView.getDuration());
                    mPbRate.setProgress((int) mOkVideoView.getDuration());
                    mFlDownloadRate.setVisibility(View.GONE);

                } else if (playbackState == RtPlayer.STATE_READY) {
                    mFlDownloadRate.setVisibility(View.GONE);

                    if (playWhenReady) {
                        mFlLoading.setVisibility(View.GONE);
                        if (!isSeekBarThreadRun) {
                            onSeekBarRun();
                        }
                    }
                } else if (playbackState == RtPlayer.STATE_BUFFERING) {
                    mFlDownloadRate.setVisibility(View.VISIBLE);
                }
                Log.w(TAG, "" + playWhenReady + "/" + playbackState);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            }
        });
        mPbRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOkVideoView.getPlaybackState() == RtPlayer.STATE_READY) {
                    mOkVideoView.seekTo(seekBar.getProgress());
                    long duration = mOkVideoView.getDuration();
                    long currentPosition = mOkVideoView.getCurrentPosition();
                    showPlayerTime(duration, currentPosition);
                }
            }
        });
        mUri = Uri.parse(AppUtils.getVideoPath(mVideoUrl));
        mOkVideoView.setVideoUri(mUri);
        mOkVideoView.seekTo(currentPosition);

    }

    private void initDate() {
        mVideoUrl = getIntent().getStringExtra("videoUrl");
        mSourceId = getIntent().getStringExtra("");
        mSourceType = getIntent().getStringExtra("");
        mSourceTitle = getIntent().getStringExtra("");
    }

    private void onSeekBarRun() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                isSeekBarThreadRun = true;

                runOnUiThread(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if (mOkVideoView != null) {
                            mTvDownloadRate.setText(mOkVideoView.getBufferedPercentage() + "%");

                            if (mOkVideoView.getPlaybackState() != RtPlayer.STATE_READY) {
                                return;
                            }

                            long duration = mOkVideoView.getDuration();
                            long currentPosition = mOkVideoView.getCurrentPosition();

                            mPbRate.setMax((int) duration);
                            mPbRate.setProgress((int) currentPosition);

                            showPlayerTime(duration, currentPosition);
                        }
                    }
                });
                if (isPlayOver) {
                    isSeekBarThreadRun = false;
                    return;
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private synchronized void showPlayerTime(long duration, long currentPosition) {
        String totalTime = GlobalUtils.getTimeFromMillisecond(duration);
        String currentTime = GlobalUtils.getTimeFromMillisecond(currentPosition);
        mTvRate.setText(currentTime + " / " + totalTime);
    }

    @Override
    public void onBackPressed() {
        currentPosition = mOkVideoView.getCurrentPosition();
        Intent mIntent = new Intent();
        mIntent.putExtra("currentPosition", currentPosition);
        setResult(RESULT_OK, mIntent);

        if (mOkVideoView != null) {
            mOkVideoView.onDestroy();
        }
        isPlayOver = true;
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOkVideoView.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        mOkVideoView.onNewIntent();
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOkVideoView.onResume(mUri);
    }

    @OnTouch(R.id.video_play_content)
    boolean onDanmakuViewTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                mLlBrightness.setVisibility(View.GONE);
                mLlVolume.setVisibility(View.GONE);
                break;
            default:
                break;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @OnClick(R.id.video_back)
    void onVideoBack(View v) {
        currentPosition = mOkVideoView.getCurrentPosition();
        Intent mIntent = new Intent();
        mIntent.putExtra("currentPosition", currentPosition);
        setResult(RESULT_OK, mIntent);
        if (mOkVideoView != null) {
            mOkVideoView.onDestroy();
        }
        isPlayOver = true;
        finish();
    }


    @OnClick(R.id.video_hd)
    void onVideoHD(View v) {
        GlobalUtils.showToastShort(this, "啊啊啊");
    }


    @OnClick(R.id.iv_play)
    void onPlayClick(View v) {
        if (mOkVideoView.getPlaybackState() == RtPlayer.STATE_READY) {
            boolean playWhenReady = mOkVideoView.getPlayWhenReady();
            if (playWhenReady) {
                mOkVideoView.setPlayWhenReady(false);
                mIvPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
            } else {
                mOkVideoView.setPlayWhenReady(true);
                mIvPlay.setBackgroundResource(android.R.drawable.ic_media_play);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 手势监听
     */
    public class CustomTouchListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mTitleBar.getVisibility() == View.INVISIBLE && mControllerBar.getVisibility() == View.INVISIBLE) {
                mTitleBar.setVisibility(View.VISIBLE);
                mControllerBar.setVisibility(View.VISIBLE);
            } else {
                mTitleBar.setVisibility(View.INVISIBLE);
                mControllerBar.setVisibility(View.INVISIBLE);
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        //在ACTION_MOVE触发
        //移动的像素
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float oldX = e1.getX();
            float oldY = e1.getY();
            int newY = (int) e2.getY();

            int width = mOkVideoView.getWidth();
            int height = mOkVideoView.getHeight();

            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
//                gesture_progress_layout.setVisibility(View.VISIBLE);
                mLlBrightness.setVisibility(View.GONE);
                mLlVolume.setVisibility(View.GONE);
//                GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
            } else {
                if (oldX < width / 2) {
                    //亮度
                    float percent = SysUtils.onBrightnessSlide(VideoPlayActivity.this, (oldY - newY) / width);
                    mLlBrightness.setVisibility(View.VISIBLE);
                    mTvBrightness.setText((int) (percent * 100) + "%");
                } else if (oldX > width / 2) {
                    //音量
                    int percent = SysUtils.onVolumeSlide(mAudioManager, GlobalUtils.dip2px(VideoPlayActivity.this, 5), distanceY, (oldY - newY) / width);
                    mLlVolume.setVisibility(View.VISIBLE);
                    mTvVolume.setText(String.valueOf(percent) + "%");
                }
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        //在ACTION_UP时才会触发
        //坐标轴上的移动速度，像素/秒
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

//        @Override
//        public boolean onContextClick(MotionEvent e) {
//            return super.onContextClick(e);
//        }
    }


}

