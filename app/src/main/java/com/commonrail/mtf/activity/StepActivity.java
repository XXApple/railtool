package com.commonrail.mtf.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.R;
import com.commonrail.mtf.base.BaseActivity;
import com.commonrail.mtf.po.ModuleItem;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.po.Step;
import com.commonrail.mtf.po.StepList;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.common.SDCardUtils;
import com.commonrail.mtf.util.retrofit.RxUtils;
import com.commonrail.rtplayer.RtPlayer;
import com.commonrail.rtplayer.listener.RtPlayerListener;
import com.commonrail.rtplayer.view.RtVideoView;
import com.facebook.drawee.view.SimpleDraweeView;

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
    @Bind(R.id.home_btn)
    LinearLayout homeBtn;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;

    @Bind(R.id.rt_video_view)
    RtVideoView mOkVideoView;
    @Bind(R.id.videoPicUrl)
    SimpleDraweeView videoPicUrl;
    @Bind(R.id.injectorTv)
    TextView injectorTv;
    @Bind(R.id.xhTv)
    TextView xhTv;
    @Bind(R.id.dispStepNameTv)
    TextView dispStepNameTv;
    @Bind(R.id.measToolNumEt)
    EditText measToolNum;
    @Bind(R.id.measToolPic)
    SimpleDraweeView measToolPic;
    @Bind(R.id.testSpecTv)
    TextView testSpecTv;
    @Bind(R.id.picUrlImg)
    SimpleDraweeView picUrlImg;

    private boolean isPlayOver = false;
    private Uri mUri;


    private int curStepOrder = 0;
    private RtApi api;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(R.string.title_activity_main);
        measToolNum.clearFocus();
        api = RxUtils.createApi(RtApi.class, Config.BASE_URL);

        String injectorType = getIntent().getStringExtra("injectorType");
        String language = getIntent().getStringExtra("language");
        int moduleId = getIntent().getIntExtra("moduleId", 0);
        String xh = getIntent().getStringExtra("xh");
        getRepairStep(injectorType, language, moduleId, xh);


//        danmuku();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_step;
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

    private void getRepairStep(String injectorType, String language, int moduleId, String xh) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("injectorType", injectorType);
        map.put("language", language);
        map.put("moduleId", moduleId);
        if (!TextUtils.isEmpty(xh)) {
            map.put("xh", xh);
            xhTv.setText(String.valueOf(xh));
        }
        injectorTv.setText(injectorType);
        L.e(map.toString());

        subscription.add(api.getRepairStep(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Result<StepList>>() {
                    @Override
                    public void call(Result<StepList> t) {
                        L.e("getRepairStep " + t.getStatus() + t.getMsg() + "" + t.getData().toString());
                        if (t.getStatus() == 200) {
                            Toast.makeText(getApplicationContext(), t.getMsg(), Toast.LENGTH_SHORT).show();
                            StepList mStepList = t.getData();
                            if (mStepList != null) {
                                L.v("ModuleName:" + mStepList.getModule().getModuleName());
                                ModuleItem mItem = mStepList.getModule();


                                Step mStep = mStepList.getStepList().get(curStepOrder);
                                if (mStep == null) {
                                    return;
                                }
                                dispStepNameTv.setText(mStep.getDispStepName());
                                measToolNum.setText(mStep.getMeasToolNum());
                                measToolPic.setImageURI(AppUtils.getFileFrescoUri(mStep.getMeasToolPic()));
                                testSpecTv.setText(mStep.getTestSpec());
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

        IntentUtils.enterVideoPlayActivity(this, 0);
    }

    @Override
    protected void onPause() {
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

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
        IntentUtils.enterDeviceScanActivity(this);
    }


    public void logLongString(String veryLongString) {
        int maxLogSize = 1000;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Log.v(TAG, veryLongString.substring(start, end));
        }
    }

}
