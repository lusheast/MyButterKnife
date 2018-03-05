package com.butterknife;

import android.support.annotation.UiThread;

/**
 * Created by zsd on 2018/2/24 15:27
 * desc:
 */

public interface Unbinder {
    @UiThread
    void unbind();

    Unbinder EMPTY = new Unbinder() {
        @Override
        public void unbind() {
        }
    };
}
