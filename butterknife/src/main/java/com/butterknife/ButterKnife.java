package com.butterknife;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * Created by zsd on 2018/2/23 17:36
 * desc:
 */

public class ButterKnife {

    public static Unbinder bind(Activity activity){
     // xxxx_ViewBinding viewBinding = new xxxx_ViewBinding(this);
        try {
            //在这这里我们先拿到ButterKnife自动生成的那个类
            Class<? extends Unbinder> bindClassName = (Class<? extends Unbinder>) Class.forName(activity.getClass().getName()+"_ViewBinding");
            //找到他的构造函数
            Constructor<? extends Unbinder> bindConstructor = bindClassName.getDeclaredConstructor(activity.getClass());
            Unbinder unbinder = bindConstructor.newInstance(activity);
            return unbinder;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Unbinder.EMPTY;
    }
}


//ButterKnife自动生成的源码如下
//public class ActivityAddBankCard_ViewBinding implements Unbinder {
//    private ActivityAddBankCard target;
//
//    @UiThread
//    public ActivityAddBankCard_ViewBinding(ActivityAddBankCard target) {
//        this(target, target.getWindow().getDecorView());
//    }
//
//    @UiThread
//    public ActivityAddBankCard_ViewBinding(final ActivityAddBankCard target, View source) {
//        this.target = target;
//
//        target.mNtb = Utils.findRequiredViewAsType(source, R.id.ntb, "field 'mNtb'", NormalTitleBar.class);
//
//    }
//
//    @Override
//    @CallSuper
//    public void unbind() {
//        ActivityAddBankCard target = this.target;
//        if (target == null) throw new IllegalStateException("Bindings already cleared.");
//        this.target = null;
//        view2131755170 = null;
//    }
//}