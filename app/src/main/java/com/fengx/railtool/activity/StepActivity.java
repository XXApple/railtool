package com.fengx.railtool.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fengx.railtool.R;
import com.fengx.railtool.base.BaseActivity;
import com.fengx.railtool.po.Result;
import com.fengx.railtool.po.StepList;
import com.fengx.railtool.util.Api.Config;
import com.fengx.railtool.util.Api.RtApi;
import com.fengx.railtool.util.IntentUtils;
import com.fengx.railtool.util.common.GlobalUtils;
import com.fengx.railtool.util.common.L;
import com.fengx.railtool.util.common.SDCardUtils;
import com.fengx.railtool.util.retrofit.RxUtils;
import com.fengx.rtplayer.RtPlayer;
import com.fengx.rtplayer.listener.RtPlayerListener;
import com.fengx.rtplayer.view.RtVideoView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：16/1/12 下午10:48
 * 修改人：wengyiming
 * 修改时间：16/1/12 下午10:48
 * 修改备注：
 */
public class StepActivity extends BaseActivity {
    @Bind(R.id.iv_play)
    ImageView mIvPlay;

    @Bind(R.id.rt_video_view)
    RtVideoView mOkVideoView;

    private boolean isPlayOver = false;
    private Uri mUri;


    private RtApi api;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_step;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);


        String injectorType = getIntent().getStringExtra("injectorType");
        String language = getIntent().getStringExtra("language");
        int moduleId = getIntent().getIntExtra("moduleId", 0);
        String xh = getIntent().getStringExtra("xh");
        getRepairStep(injectorType, language, moduleId, xh);


        danmuku();
    }


    private void danmuku() {

        mOkVideoView.addListener(new RtPlayerListener() {
            @Override
            public void onStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == RtPlayer.STATE_ENDED) {
                    isPlayOver = true;

                } 
//                else if (playbackState == RtPlayer.STATE_READY) {
//                } else if (playbackState == RtPlayer.STATE_BUFFERING) {
//                }
                Log.w(TAG, "" + playWhenReady + "/" + playbackState);
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            }
        });
//        String videourl = "http://7xkbzx.com1.z0.glb.clouddn.com/SampleVideo_1080x720_20mb.mp4";

        String videourl = SDCardUtils.getSDCardPath() + "/preinstall/video/Honor.mp4";
        mUri = Uri.parse(videourl);
        mOkVideoView.setVideoUri(mUri);


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

    @OnClick(R.id.rt_video_view)
    void onVideoViewClick(View v) {
        IntentUtils.enterVideoPlayActivity(this, "");
    }


    private void getRepairStep(String injectorType, String language, int moduleId, String xh) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", language);
        map.put("moduleId", moduleId);
        if (!TextUtils.isEmpty(xh))
            map.put("xh", xh);

        L.e(map.toString());

        subscription.add(api.getRepairStep(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<StepList>>() {
                    @Override
                    public void call(Result<StepList> t) {
                        L.e("getRepairStep " + t.getStatus() + t.getMsg());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            StepList mStepList = t.getData();
                            if (mStepList != null) {
                                L.v("ModuleName:" + mStepList.getModule().getModuleName() + "\n StepList size:" + mStepList.getStepList().size() + "");
                            }
                        } else {
                            GlobalUtils.showToastShort(StepActivity.this, getString(R.string.net_error));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(StepActivity.this, getString(R.string.net_error));
                    }
                }));
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

    @Override
    protected void onPause() {
        super.onPause();
        mOkVideoView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOkVideoView != null) {
            mOkVideoView.onDestroy();
        }
        isPlayOver = true;
    }


}
