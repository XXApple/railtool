package com.fengx.railtool.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.fengx.railtool.R;
import com.fengx.railtool.activity.MainActivity;
import com.fengx.railtool.activity.ModuleListActivity;
import com.fengx.railtool.activity.Step2Activity;
import com.fengx.railtool.activity.StepActivity;
import com.fengx.railtool.activity.VideoPlayActivity;
import com.fengx.railtool.activity.bluetooth.DeviceControlActivity;
import com.fengx.railtool.activity.bluetooth.DeviceScanActivity;


/**
 * 项目名称：railtool
 * 类描述：
 * 创建人：wengyiming
 * 创建时间：15/11/16 下午10:34
 * 修改人：wengyiming
 * 修改时间：15/11/16 下午10:34
 * 修改备注：
 */
public class IntentUtils {
    private final static int NO_ANIMOTION = -1;

    public static void enterModuleListActivity(Activity context, String injectorType, String language) {
        Intent intent = new Intent(context, ModuleListActivity.class);
        intent.putExtra("injectorType", injectorType);
        intent.putExtra("language", language);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }

    public static void enterMainActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }
    public static void enterStepActivity(Activity context, String injectorType, String language, int moduleId, String xh) {
        Intent intent = new Intent(context, StepActivity.class);
        intent.putExtra("injectorType", injectorType);
        intent.putExtra("language", language);
        intent.putExtra("moduleId", moduleId);
        intent.putExtra("xh", xh);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }

    public static void enterStep2Activity(Activity context, String injectorType, String language, int moduleId, String xh,String mDeviceName,String mDeviceAddress) {
        Intent intent = new Intent(context, Step2Activity.class);
        intent.putExtra("injectorType", injectorType);
        intent.putExtra("language", language);
        intent.putExtra("moduleId", moduleId);
        intent.putExtra("xh", xh);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mDeviceName);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }
    public static void enterVideoPlayActivity(Context activity,String sourceId) {
        Intent intent = new Intent(activity, VideoPlayActivity.class);

        activity.startActivity(intent);
    }

    public static void enterDeviceScanActivity(Context activity) {
        Intent intent = new Intent(activity, DeviceScanActivity.class);

        activity.startActivity(intent);
    }

}
