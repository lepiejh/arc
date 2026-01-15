package com.ved.framework.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity;
import com.ved.framework.R;
import com.ved.framework.utils.KLog;
import com.ved.framework.utils.ScreenUtils;
import com.ved.framework.utils.UIUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;

public class ImmersionBarBaseActivity extends RxAppCompatActivity implements ViewTreeObserver.OnGlobalLayoutListener{
    private ImmersionBar mImmersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
        if (!isTaskRoot()){
            Intent intent = getIntent();
            String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && UIUtils.equals(intentAction,Intent.ACTION_MAIN)){
                finish();
            }
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
//            HookSystem.Companion.getInstance().hookSystemHandler();
        }else {
            //如果是8.0系统的手机，并且认为是透明主题的Activity
            if (Build.VERSION.SDK_INT == 26 && this.isTranslucentOrFloating()) {
                //通过反射取消方向的设置，这样绕开系统的检查，避免闪退
                boolean result = this.fixOrientation();
                KLog.i("only_opaque","result : "+result);
            }
        }
        super.onCreate(savedInstanceState);
        if (isRegular()) {
            if (!ScreenUtils.isTabletDevice()){
                if (getRequestedOrientation() == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        }
        initStatusBar();
    }

    public boolean isRegular(){
        return true;
    }

    //通过反射判断是否是透明页面
    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    //通过反射将方向设置为 SCREEN_ORIENTATION_UNSPECIFIED，绕开系统的检查
    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            if (o != null) {
                o.screenOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            }
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取状态栏字体颜色
     */
    public boolean statusBarDarkFont() {
        //返回false表示白色字体
        return true;
    }

    public int statusBarColor(){
        return R.color.colorPrimary;
    }

    public void initStatusBar() {
        //初始化沉浸式状态栏
        if (isStatusBarEnabled()) {
            statusBarConfig().init();
        }
    }

    /**
     * 是否使用沉浸式状态栏
     */
    public boolean isStatusBarEnabled() {
        return true;
    }

    public boolean statusBarColorDef(){
        return true;
    }

    /**
     * 设置状态栏背景颜色
     */
    public void setStatusBarColor(int color){
        if (mImmersionBar != null && statusBarColorDef()) {
            mImmersionBar.statusBarColor(color).init();
        }
    }

    /**
     * 设置状态栏字体的颜色
     */
    public void setStatusBarDarkFont(boolean blackFont){
        if (mImmersionBar != null) {
            mImmersionBar.statusBarDarkFont(blackFont).init();
        }
    }

    /**
     * 初始化沉浸式状态栏
     */
    private ImmersionBar statusBarConfig() {
        //状态栏沉浸
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.statusBarDarkFont(statusBarDarkFont());   //默认状态栏字体颜色为黑色
        if (statusBarColorDef()) {
            mImmersionBar.statusBarColor(statusBarColor());
        }
        switch (getStatusBarHide()){
            case 0 :
                //隐藏状态栏
                mImmersionBar.hideBar(BarHide.FLAG_HIDE_STATUS_BAR);
                break;
            case 1 :
                //隐藏导航栏
                mImmersionBar.hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR);
                break;
            case 2 :
                //隐藏状态栏和导航栏
                mImmersionBar.hideBar(BarHide.FLAG_HIDE_BAR);
                break;
            default:
                //显示状态栏和导航栏
                mImmersionBar.hideBar(BarHide.FLAG_SHOW_BAR);
                break;
        }
        mImmersionBar.keyboardEnable(false, WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);  //解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
        //必须设置View树布局变化监听，否则软键盘无法顶上去，还有模式必须是SOFT_INPUT_ADJUST_PAN
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(this);
        return mImmersionBar;
    }

    public int getStatusBarHide(){
        return 3;
    }

    @Override
    public void onGlobalLayout() {

    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDestroy();
    }
}
