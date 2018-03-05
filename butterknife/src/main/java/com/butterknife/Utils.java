package com.butterknife;

import android.app.Activity;
import android.util.Log;
import android.view.View;

/**
 * Created by zsd on 2018/2/24 16:48
 * desc:
 */

public class Utils {


    public static final <T extends View> T findViewById(Activity activity, int viewId) {
        Log.i("test",activity.findViewById(viewId).getClass().getName());
        return activity.findViewById(viewId);
    }
}
