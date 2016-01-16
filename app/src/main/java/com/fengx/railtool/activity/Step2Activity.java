package com.fengx.railtool.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.fengx.railtool.R;
import com.fengx.railtool.base.BaseActivity;
import com.fengx.railtool.po.ModuleItem;
import com.fengx.railtool.po.Result;
import com.fengx.railtool.po.Step;
import com.fengx.railtool.po.StepList;
import com.fengx.railtool.util.Api.Config;
import com.fengx.railtool.util.Api.RtApi;
import com.fengx.railtool.util.IntentUtils;
import com.fengx.railtool.util.common.AppUtils;
import com.fengx.railtool.util.common.GlobalUtils;
import com.fengx.railtool.util.common.L;
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
public class Step2Activity extends BaseActivity {

    @Bind(R.id.rightImg)
    ImageView rightImg;
    @Bind(R.id.rightBtn)
    TextView rightBtn;
    @Bind(R.id.home_btn)
    LinearLayout homeBtn;
    @Bind(R.id.app_bar)
    AppBarLayout appBar;
    @Bind(R.id.videoPicUrl)
    SimpleDraweeView videoPicUrl;
    @Bind(R.id.injectorTv)
    TextView injectorTv;
    @Bind(R.id.xhTv)
    TextView xhTv;
    @Bind(R.id.dispStepNameTv)
    TextView dispStepNameTv;
    @Bind(R.id.chooseTv)
    TextView chooseTv;
    @Bind(R.id.iv_play)
    ImageView mIvPlay;
    @Bind(R.id.rt_video_view)
    RtVideoView mOkVideoView;
    @Bind(R.id.testSpecTv2)
    TextView testSpecTv2;
    @Bind(R.id.angleTv)
    TextView angleTv;
    @Bind(R.id.mkzTv)
    TextView mkzTv;
    @Bind(R.id.ljfwTv)
    TextView ljfwTv;
    @Bind(R.id.ljTv)
    TextView ljTv;
    @Bind(R.id.preBtn)
    TextView preBtn;
    @Bind(R.id.nextBtn)
    TextView nextBtn;

    @Bind(R.id.measDisp)
    TextView measDisp;
    @Bind(R.id.suggestDisp)
    TextView suggestDisp;

    @Bind(R.id.measToolNumEt)
    TextView measToolNumEt;
    @Bind(R.id.measToolPic)
    SimpleDraweeView measToolPic;
    @Bind(R.id.picUrl)
    SimpleDraweeView picUrl;
    @Bind(R.id.type2Line)
    LinearLayout type2Line;
    @Bind(R.id.type1Line)
    LinearLayout type1Line;
    @Bind(R.id.testSpecTv1)
    TextView testSpecTv1;

    @Bind(R.id.testResultLine)
    LinearLayout testResultLine;


    @Bind(R.id.progress)
    ProgressBar progress;
    @Bind(R.id.rootLine)
    LinearLayout rootLine;

    private boolean isPlayOver = false;
    private Uri mUri;

    private StepList mStepList;

    private int curStepOrder = 0;
    private RtApi api;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public int getLayoutRes() {
        return R.layout.activity_step2;
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
        progress.setVisibility(View.VISIBLE);
        rootLine.setVisibility(View.GONE);
        getRepairStep(injectorType, language, moduleId, xh);


//        danmuku();
    }


    private void startVideo(String videoName) {

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
        mUri = Uri.parse(AppUtils.getVideoPath(videoName));
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
                            mStepList = t.getData();
                            if (mStepList != null) {
                                ModuleItem mItem = mStepList.getModule();
                                L.v("ModuleName:" + mItem.getModuleName());
                                progress.setVisibility(View.GONE);
                                rootLine.setVisibility(View.VISIBLE);
                                Step mStep = checkStep(curStepOrder);
                                if (mStep == null) return;
                                startVideo(mStep.getVideoUrl());
                                setStepOrderInfo(curStepOrder);
                            }
                        } else {
                            GlobalUtils.showToastShort(Step2Activity.this, getString(R.string.net_error));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        progress.setVisibility(View.GONE);
                        rootLine.setVisibility(View.GONE);
                        L.e("" + throwable.toString());
                        GlobalUtils.showToastShort(Step2Activity.this, getString(R.string.net_error));
                    }
                }));
    }

    private void setStepOrderInfo(final int curStepOrder) {
        Step mStep = checkStep(curStepOrder);
        if (mStep == null) return;
        if (TextUtils.equals(mStep.getPageType(), "1")) {
            measDisp.setVisibility(View.VISIBLE);
            suggestDisp.setVisibility(View.VISIBLE);
            measToolNumEt.setVisibility(View.VISIBLE);
            measToolPic.setVisibility(View.VISIBLE);
            picUrl.setVisibility(View.VISIBLE);
            measToolNumEt.setVisibility(View.VISIBLE);
            type1Line.setVisibility(View.VISIBLE);
            testSpecTv1.setVisibility(View.VISIBLE);
            testResultLine.setVisibility(View.VISIBLE);
            chooseTv.setVisibility(View.VISIBLE);
            type2Line.setVisibility(View.GONE);

            measToolNumEt.setText(mStep.getMeasToolNum());
            measToolPic.setImageURI(AppUtils.getFileFrescoUri(mStep.getMeasToolPic()));
            picUrl.setImageURI(AppUtils.getFileFrescoUri(mStep.getPicUrl()));
            measDisp.setText(mStep.getMeasDisp());
            suggestDisp.setText(mStep.getSuggestDisp());
            testSpecTv1.setText(mStep.getTestSpec());
        } else {
            measDisp.setVisibility(View.GONE);
            suggestDisp.setVisibility(View.GONE);
            chooseTv.setVisibility(View.GONE);
            measToolNumEt.setVisibility(View.GONE);
            measToolPic.setVisibility(View.GONE);
            picUrl.setVisibility(View.GONE);
            type1Line.setVisibility(View.GONE);
            picUrl.setVisibility(View.GONE);
            testResultLine.setVisibility(View.GONE);
            testSpecTv2.setVisibility(View.VISIBLE);
            type2Line.setVisibility(View.VISIBLE);


            angleTv.setText(mStep.getAngle());
            mkzTv.setText(mStep.getMkz());
            ljfwTv.setText(mStep.getLjfw());
            ljTv.setText(mStep.getLj());
            testSpecTv2.setText(mStep.getTestSpec());
        }


        dispStepNameTv.setText(mStep.getDispStepName());
        videoPicUrl.setImageURI(AppUtils.getFileFrescoUri(mStep.getVideoPicUrl()));


    }

    @Nullable
    private Step checkStep(final int curStepOrder) {
        if (mStepList == null) {
            return null;
        }
        Step mStep = mStepList.getStepList().get(curStepOrder);
        if (mStep == null) {
            return null;
        }
        return mStep;
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

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
        IntentUtils.enterDeviceScanActivity(this);
    }

    @OnClick(R.id.preBtn)
    public void setPreBtn(View mView) {
        int maxSteps = mStepList.getStepList().size();
        --curStepOrder;
        if (curStepOrder < 0) {
            curStepOrder = maxSteps - 1;
        }

        if (curStepOrder >= maxSteps) {
            curStepOrder = 0;
        }
        setStepOrderInfo(curStepOrder);
    }

    @OnClick(R.id.nextBtn)
    public void setNxtBtn(View mView) {
        ++curStepOrder;
        int maxSteps = mStepList.getStepList().size();
        if (curStepOrder > maxSteps - 1) {
            curStepOrder = 0;
        }
        setStepOrderInfo(curStepOrder);
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
