package com.butterknife.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zsd on 2018/2/23 17:34
 * desc:
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)//编译时注解
public @interface BindView {
    int value();
}
