package com.ved.framework.utils;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by ved on 2017/5/14.
 * 常用工具类
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Application context) {
        com.ved.framework.utils.bland.code.Utils.init(context);
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        return com.ved.framework.utils.bland.code.Utils.getApplication().getApplicationContext();
    }
}