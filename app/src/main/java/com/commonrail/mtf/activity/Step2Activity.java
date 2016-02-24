package com.commonrail.mtf.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonrail.mtf.AppClient;
import com.commonrail.mtf.R;
import com.commonrail.mtf.activity.bluetooth.BluetoothLeService;
import com.commonrail.mtf.activity.bluetooth.SampleGattAttributes;
import com.commonrail.mtf.base.BaseActivity;
import com.commonrail.mtf.po.ModuleItem;
import com.commonrail.mtf.po.Result;
import com.commonrail.mtf.po.Step;
import com.commonrail.mtf.po.StepList;
import com.commonrail.mtf.po.Value;
import com.commonrail.mtf.util.Api.Config;
import com.commonrail.mtf.util.Api.RtApi;
import com.commonrail.mtf.util.IntentUtils;
import com.commonrail.mtf.util.ReadAndCalculateUtil;
import com.commonrail.mtf.util.common.AppUtils;
import com.commonrail.mtf.util.common.GlobalUtils;
import com.commonrail.mtf.util.common.L;
import com.commonrail.mtf.util.retrofit.RxUtils;
import com.commonrail.rtplayer.RtPlayer;
import com.commonrail.rtplayer.listener.RtPlayerListener;
import com.commonrail.rtplayer.view.RtVideoView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    @Bind(R.id.home_btn)
    LinearLayout homeBtn;
    @Bind(R.id.showPicUrl)
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
    @Bind(R.id.measDispTips)
    TextView measDispTips;
    @Bind(R.id.measDispLine)
    LinearLayout measDispLine;
    @Bind(R.id.suggestDispTips)
    TextView suggestDispTips;
    @Bind(R.id.suggestDispLine)
    LinearLayout suggestDispLine;
    @Bind(R.id.measDispTest)
    TextView measDispTest;
    @Bind(R.id.suggestDispTest)
    TextView suggestDispTest;
    @Bind(R.id.rightImg)
    ImageView rightImg;
    @Bind(R.id.layout_right_bg)
    LinearLayout layoutRightBg;

    private boolean isPlayOver = false;
    private Uri mUri;

    private List<Value> values = new ArrayList<>();
    private Value curValue;

    private ModuleItem mItem = null;
    private StepList mStepList;
    private int curStepOrder = 0;
    private RtApi api = RxUtils.createApi(RtApi.class, Config.BASE_URL);


    private CompositeSubscription subscription = new CompositeSubscription();
    private String mDeviceAddress;
    private String mDeviceName;
    private BluetoothLeService mBluetoothLeService;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                if (!TextUtils.isEmpty(mDeviceName)) {
                    toolbar.setSubtitle(mDeviceName + "" + getResources().getString(R.string.connected) + mDeviceAddress);
                    L.e("已链接蓝牙设备", mDeviceName);
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                }
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                mConnected = false;
                if (!TextUtils.isEmpty(mDeviceName)) {
                    toolbar.setSubtitle(mDeviceName + "" + getResources().getString(R.string.disconnected));
                    L.e("未链接蓝牙设备", mDeviceName);
                    mProgressDialog.setMessage("蓝牙连接已断开，正在重新连接，请稍等...");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();
                    mBluetoothLeService.connect(mDeviceAddress);
                }
//                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
//                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService
                        .getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //从蓝牙取得测试结果
                String testResult = intent
                        .getStringExtra(BluetoothLeService.EXTRA_DATA);
                L.e("测试结果", testResult);
                /**
                 *
                 if (!TextUtils.isEmpty(mStep.getReadKey)) {
                 ReadAndCalculateUtil.setReadKey("");
                 }
                 if (!TextUtils.isEmpty(mStep.getSuggestCalcFun())) {
                 ReadAndCalculateUtil.setCalcKey(mStep.getSuggestCalcFun());
                 }
                 *基于前面两条都有set的时候才能计算出建议值的结果
                 *
                 */
                measDispTest.setText(testResult);

                //将测量值，计算出来的建议值和服务器提供的范围分别对比，更UI上的值的提示


                Step mStep = mStepList.getStepList().get(curStepOrder);
                String result = testResult.replace("mm", "");
                curValue.setStepId(Integer.parseInt(mStep.getStepId()));
                curValue.setStepNum(Integer.parseInt(mStep.getStepOrder()));

                try {
                    checkMeasResult(mStep, result);
                    checkSuggetCalc(mStep);
                } catch (NumberFormatException e) {
                    L.e(e.toString());
                }


                if (curStepOrder == mStepList.getStepList().size() - 1) {
                    //提交测试结果
                    if (mItem == null) {
                        return;
                    }
                    int moduleId = mItem.getId();
                    String injectorType = injectorTv.getText().toString().trim();
                    HashMap<String, Object> mMap = new HashMap<>();
                    mMap.put("moduleId", moduleId);
                    mMap.put("injectorType", injectorType);
                    mMap.put("values", values);
                    uploadMesResult(mMap);
                }
            }
        }
    };

    private void checkMeasResult(final Step mStep, final String mResult) {
        if (TextUtils.isEmpty(mResult)) {
            measDispTips.setVisibility(View.GONE);
            measDispTest.setText("0.000");
            return;
        }
        double meadispResult = Double.parseDouble(mResult);
        L.e("测试结果：" + meadispResult);
        meadispResult = Math.abs(meadispResult);//取绝对值
        if (meadispResult == 0.001) {
            L.e("该结果几乎等于0，已过滤");
            return;
        }
        L.e("value setMeasResult" + meadispResult);
        curValue.setMeasResult(meadispResult);
        ReadAndCalculateUtil.handleReadValue(mResult);
        //解析测试值的范围
        String measdiap = mStep.getMeasRange();
        L.e("测试值范围：" + measdiap);
        String[] measdiapR = measdiap.split("-");
        double measdiapR1 = Double.parseDouble(measdiapR[0]);
        double measdiapR2 = Double.parseDouble(measdiapR[1]);

        L.e("测试范围1：" + measdiapR1);
        L.e("测试范围2：" + measdiapR2);
        measDispTips.setVisibility(View.VISIBLE);
        if (meadispResult >= measdiapR1 && meadispResult <= measdiapR2) {
            measDispTips.setText("您的测试符合正常范围...");
        } else if (meadispResult < measdiapR1) {
            measDispTips.setText("您的测试存在错误，测量值偏小...");
        } else {
            measDispTips.setText("您的测试存在错误，测量值偏大...");
        }
    }

    private void checkSuggetCalc(final Step mStep) {
        String sgstKey = mStep.getSuggestCalcFun();
        if (!TextUtils.isEmpty(sgstKey)) {
            if (ReadAndCalculateUtil.DATA_MAP.get(sgstKey) != null) {
                String sgtStr = ReadAndCalculateUtil.DATA_MAP.get(sgstKey);
                if (TextUtils.isEmpty(sgtStr)) {
                    suggestDispTest.setText("0.000");
                    return;
                }
                double sgt = Double.parseDouble(sgtStr);
                L.e("value setCalcResult" + sgt);
                curValue.setCalcResult(sgt);
                suggestDispTest.setText(String.valueOf(sgt));
                //解析建议值的范围
                String sgstRange = mStep.getSgstRange();
                L.e("建议值范围" + sgstRange);
                String[] sgstR = sgstRange.split("-");
                double sgst1 = Double.parseDouble(sgstR[0]);
                double sgst2 = Double.parseDouble(sgstR[1]);
                L.e("建议值范围1：" + sgst1);
                L.e("建议值范围1：" + sgst2);

                L.e("建议值结果：" + sgt);
                suggestDispTips.setVisibility(View.VISIBLE);
                //将计算结果和范围对比
                if (sgt >= sgst1 && sgt <= sgst2) {
                    suggestDispTips.setText("您的测试符合正常范围...");
                } else if (sgt < sgst1) {
                    suggestDispTips.setText("您的测试存在错误，垫片超出合理范围...");
                } else {
                    suggestDispTips.setText("您的测试存在错误，垫片超出合理范围...");
                }

            } else {
                suggestDispTips.setVisibility(View.GONE);
                suggestDispTips.setText("");
                suggestDispTest.setText("0.000");
            }
        } else {
            suggestDispTips.setVisibility(View.GONE);
            suggestDispTips.setText("");
            suggestDispTest.setText("0.000");
        }
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
        String moduleName = getIntent().getStringExtra("moduleName");
        String xh = getIntent().getStringExtra("xh");
        progress.setVisibility(View.VISIBLE);
        rootLine.setVisibility(View.GONE);

        final String mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        L.e("mDeviceAddress:" + mDeviceAddress);
        toolbar.setTitle(" " + injectorType + "  " + moduleName);
        ReadAndCalculateUtil.init();
        getRepairStep(injectorType, language, moduleId, xh);

        Intent gattServiceIntent = new Intent(getActivity(), BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

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
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
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
                .map(new Func1<Result<StepList>, StepList>() {
                    @Override
                    public StepList call(Result<StepList> t) {
                        L.e("getRepairStep " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(AppClient.getInstance(), getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<StepList>() {
                    @Override
                    public void call(StepList t) {
                        mStepList = t;
                        if (mStepList != null) {
                            mItem = mStepList.getModule();
                            L.v("ModuleName:" + mItem.toString());
                            progress.setVisibility(View.GONE);
                            rootLine.setVisibility(View.VISIBLE);
                            Step mStep = checkStep(curStepOrder);
                            if (mStep == null) return;
                            L.e("mStep.getVideoUrl()" + mStep.getVideoUrl());
                            startVideo(mStep.getVideoUrl());
                            setStepOrderInfo(curStepOrder);


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

    private void startVideo(String videoName) {

        mOkVideoView.addListener(new RtPlayerListener() {
            @Override
            public void onStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == RtPlayer.STATE_ENDED) {
                    isPlayOver = true;
                    mOkVideoView.setVideoUri(mUri);
                } else if (playbackState == RtPlayer.STATE_READY) {
                    GlobalUtils.showToastShort(Step2Activity.this, "视频准备中...");
                } else if (playbackState == RtPlayer.STATE_BUFFERING) {
                    GlobalUtils.showToastShort(Step2Activity.this, "视频缓冲中...");
                }
                Log.w(TAG, "" + playWhenReady + "/" + playbackState);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(Step2Activity.this, "视频打开出错", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

            }
        });
        mUri = Uri.parse(AppUtils.getVideoPath(videoName));
        mOkVideoView.setVideoUri(mUri);
    }

    private void setStepOrderInfo(final int curStepOrder) {
        if (curValue != null) {
//            L.e("添加一个value: stepId:" + curValue.getStepId() + "stepNum: " + curValue.getStepNum()
//                            + "measResult: " + curValue.getMeasResult()
//                            + "valcResult: " + curValue.getCalcResult()
//            );
//
            L.e("添加一个value: ");
            values.add(curValue);
            curValue = null;
        }

        Step mStep = checkStep(curStepOrder);
        if (mStep == null) return;
        L.e("mStep.toString()" + mStep.toString());
        if (TextUtils.equals(mStep.getPageType(), "1")) {
            layoutRightBg.setBackground(ContextCompat.getDrawable(this, R.drawable.dv_white_shape_bg));
            measToolNumEt.setVisibility(View.VISIBLE);
            measToolPic.setVisibility(View.VISIBLE);
            picUrl.setVisibility(View.VISIBLE);
            type1Line.setVisibility(View.VISIBLE);
            testSpecTv1.setVisibility(View.VISIBLE);
            testSpecTv2.setVisibility(View.GONE);
            testResultLine.setVisibility(View.VISIBLE);
            chooseTv.setVisibility(View.VISIBLE);
            type2Line.setVisibility(View.GONE);

            measToolNumEt.setText(mStep.getMeasToolNum());
            measToolPic.setImageURI(AppUtils.getFileFrescoUri(mStep.getMeasToolPic()));
            picUrl.setImageURI(AppUtils.getFileFrescoUri(mStep.getPicUrl()));
            rightImg.setImageResource(R.drawable.img_tips);

            if (!TextUtils.isEmpty(mStep.getSuggestDisp())) {
                suggestDispLine.setVisibility(View.VISIBLE);
                suggestDisp.setText(mStep.getSuggestDisp());
            } else {
                suggestDispLine.setVisibility(View.GONE);
            }

            L.e("步骤index:" + curStepOrder + "测量值key：" + mStep.getMeasKey()
                    + "测量值范围：" + mStep.getMeasRange()
                    + "建议值key：" + mStep.getSuggestCalcFun()
                    + "建议值范围：" + mStep.getSgstRange()
                    + "\n");

            if (!TextUtils.isEmpty(mStep.getMeasKey())) {
                L.e("new 一个value");
                curValue = new Value();
                measDispLine.setVisibility(View.VISIBLE);
                measDisp.setText(mStep.getMeasDisp());
                ReadAndCalculateUtil.setReadKey(mStep.getMeasKey());
                String value = ReadAndCalculateUtil.DATA_MAP.get(mStep.getMeasKey());
                L.e(mStep.getMeasKey() + " 上次测量值:" + value);
                try {
                    checkMeasResult(mStep, value);
                } catch (NumberFormatException e) {
                    L.e(e.toString());
                }
                if (TextUtils.isEmpty(value)) {
                    measDispTest.setText("0.000");
                } else {
                    measDispTest.setText(value);
                }
            } else {
                measDispLine.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mStep.getSuggestCalcFun())) {
                suggestDispLine.setVisibility(View.VISIBLE);
                ReadAndCalculateUtil.setCalcKey(mStep.getSuggestCalcFun());
                try {
                    checkSuggetCalc(mStep);
                } catch (NumberFormatException e) {
                    L.e(e.toString());
                }
            } else {
                suggestDispLine.setVisibility(View.GONE);
            }
            testSpecTv1.setText(mStep.getTestSpec());
        } else {
            layoutRightBg.setBackgroundColor(Color.TRANSPARENT);
            measDispLine.setVisibility(View.GONE);
            suggestDispLine.setVisibility(View.GONE);
            chooseTv.setVisibility(View.GONE);
            measToolNumEt.setVisibility(View.GONE);
            measToolPic.setVisibility(View.GONE);
            picUrl.setVisibility(View.GONE);
            type1Line.setVisibility(View.GONE);
            picUrl.setVisibility(View.GONE);
            testResultLine.setVisibility(View.GONE);
            testSpecTv2.setVisibility(View.VISIBLE);
            type2Line.setVisibility(View.VISIBLE);

            rightImg.setImageResource(R.drawable.step_tips);

            angleTv.setText(mStep.getAngle());
            mkzTv.setText(mStep.getMkz());
            ljfwTv.setText(mStep.getLjfw());
            ljTv.setText(mStep.getLj());
            testSpecTv2.setText(mStep.getTestSpec());
        }


        videoPicUrl.setImageURI(AppUtils.getFileFrescoUri(mStep.getShowPicUrl()));
        dispStepNameTv.setText(mStep.getDispStepName());


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
        if (mOkVideoView != null) {
            long p = mOkVideoView.getCurrentPosition();
            IntentUtils.enterVideoPlayActivity(this, p);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (resultCode == RESULT_OK) {
            long p = data.getLongExtra("currentPosition", 0);
            if (p > 10000) {
                p = p - 2000;
            }
            if (mOkVideoView != null) {
                mOkVideoView.seekTo((p - 3));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOkVideoView.onPause();
        unregisterReceiver(mGattUpdateReceiver);
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
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @OnClick(R.id.home_btn)
    public void homeBtn(View view) {
////        IntentUtils.enterDeviceScanActivity(this);
//        Intent intent = new Intent(this, DeviceScanActivity.class);
//
//        this.startActivityForResult(intent, 0);
        onBackPressed();
    }

    @OnClick(R.id.preBtn)
    public void setPreBtn(View mView) {
//        int maxSteps = mStepList.getStepList().size();
        --curStepOrder;
        if (curStepOrder < 0) {
            curStepOrder = 0;
            return;

//            curStepOrder = maxSteps - 1;
        }
//
//        if (curStepOrder >= maxSteps) {
//            curStepOrder = 0;
//        }
        setStepOrderInfo(curStepOrder);
    }

    @OnClick(R.id.nextBtn)
    public void setNxtBtn(View mView) {
        ++curStepOrder;
        int maxSteps = mStepList.getStepList().size();
        if (curStepOrder > maxSteps - 1) {
            curStepOrder = maxSteps - 1;
            return;
        }
        setStepOrderInfo(curStepOrder);
        if (curStepOrder == mStepList.getStepList().size() - 1) {
            //提交测试结果
            if (mItem == null) {
                return;
            }
            int moduleId = mItem.getId();
            String injectorType = injectorTv.getText().toString().trim();
            HashMap<String, Object> mMap = new HashMap<>();
            mMap.put("moduleId", moduleId);
            mMap.put("injectorType", injectorType);
            mMap.put("values", values);
            uploadMesResult(mMap);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid;
        // String unknownServiceString =
        // getResources().getString(R.string.unknown_service);
        // String unknownCharaString =
        // getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        final String mLIST_NAME = "NAME";
        final String mLIST_UUID = "UUID";
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            // if (uuid.equals("0000fff0-0000-1000-8000-00805f9b34fb")) {
            currentServiceData.put(mLIST_NAME,
                    SampleGattAttributes.lookup(uuid, "Data CharaString"));
            currentServiceData.put(mLIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                // charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();

                // if (uuid.equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
                charas.add(gattCharacteristic);
                currentCharaData.put(mLIST_NAME,
                        SampleGattAttributes.lookup(uuid, "Data CharaString"));
                currentCharaData.put(mLIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                // }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
            // }
        }

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();

                if (mGattCharacteristics != null) {
                    final BluetoothGattCharacteristic characteristic = mGattCharacteristics
                            .get(3).get(3);
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a
                        // characteristic, clear
                        // it first so it doesn't update the data field on the
                        // user interface.
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);

                        // mProgressBar.setVisibility(View.GONE);

                        characteristic.getUuid();
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);
                    }

                }

            }

        }.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mProgressDialog.setMessage("正在连接蓝牙设备，请稍等...");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
//                mBluetoothLeService.disconnect();
                return true;
            case R.id.menu_refresh:
                if (!mConnected) {
                    mProgressDialog.setMessage("正在连接蓝牙设备，请稍等...");
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.show();
                    mBluetoothLeService.connect(mDeviceAddress);
                } else {
                    GlobalUtils.showToastShort(getActivity(), "蓝牙已连接");
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadMesResult(HashMap<String, Object> map) {
        Gson mGson = new Gson();
        String mJsonObject = mGson.toJson(map);
        L.e("map2JsonString: " + mJsonObject);
        L.e("map2String: " + map.toString());
        subscription.add(api.uploadMesResult(map)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Result<String>, String>() {
                    @Override
                    public String call(Result<String> t) {
                        L.e("uploadMesResult " + t.getStatus() + t.getMsg());
                        if (t.getStatus() != 200) {
                            GlobalUtils.showToastShort(Step2Activity.this, getString(R.string.net_error));
                            return null;
                        }
                        GlobalUtils.showToastShort(AppClient.getInstance(), t.getMsg());
                        return t.getData();
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String t) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        rootLine.setVisibility(View.GONE);
                        L.e("Throwable", throwable.toString());
                        GlobalUtils.showToastShort(Step2Activity.this, getString(R.string.net_error));
                    }
                }));
    }
}
