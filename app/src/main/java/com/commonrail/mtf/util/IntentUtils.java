package com.commonrail.mtf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.commonrail.mtf.R;
import com.commonrail.mtf.mvp.ui.activity.MainActivity;
import com.commonrail.mtf.mvp.ui.activity.ModuleListActivity;
import com.commonrail.mtf.mvp.ui.activity.Step2Activity;
import com.commonrail.mtf.mvp.ui.activity.VideoPlayActivity;
import com.commonrail.mtf.mvp.ui.activity.bluetooth.DeviceControlActivity;
import com.commonrail.mtf.mvp.ui.activity.bluetooth.DeviceScanActivity;


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

    public static void enterModuleListActivity(Activity context, String injectorType, String injectorIcon) {
        Intent intent = new Intent(context, ModuleListActivity.class);
        intent.putExtra("injectorType", injectorType);
        intent.putExtra("injectorIcon", injectorIcon);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }

    public static void enterMainActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }


    public static void enterStep2Activity(Activity context, String injectorType, int moduleId,String moduleName, String xh, String mDeviceAddress) {
        Intent intent = new Intent(context, Step2Activity.class);
        intent.putExtra("injectorType", injectorType);
//        intent.putExtra("language", language);
        intent.putExtra("moduleId", moduleId);
        intent.putExtra("moduleName",moduleName);
        intent.putExtra("xh", xh);
//        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, mDeviceName);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.slide_in_right, NO_ANIMOTION);
    }

    public static void enterVideoPlayActivity(Activity activity, final Uri mVideoUrl, long currentPosition) {
        Intent intent = new Intent(activity, VideoPlayActivity.class);
        intent.putExtra("currentPosition", currentPosition);
        intent.putExtra("videoUrl",mVideoUrl);
        activity.startActivityForResult(intent, 0);
    }

    public static void enterDeviceScanActivity(Context activity) {
        Intent intent = new Intent(activity, DeviceScanActivity.class);

        activity.startActivity(intent);
    }

}
