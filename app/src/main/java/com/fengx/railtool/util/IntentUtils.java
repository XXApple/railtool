package com.fengx.railtool.util;

import android.app.Activity;
import android.content.Intent;

import com.fengx.railtool.R;
import com.fengx.railtool.activity.MainActivity;
import com.fengx.railtool.activity.ModuleListActivity;
import com.fengx.railtool.activity.StepActivity;

/*********
 *********/
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


}
