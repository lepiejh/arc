package com.ved.framework.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.android.ActivityEvent;
import com.ved.framework.bus.event.eventbus.MessageEvent;
import com.ved.framework.permission.IPermission;
import com.ved.framework.utils.KLog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends ImmersionBarBaseActivity implements IBaseView<V, VM> {
    private final BaseView<V, VM> baseView = new BaseView<>(this);

    protected V binding;
    private VM viewModel;
    private ViewModelProxy<VM> viewModelProxy;

    protected VM getViewModel() {
        if (null == viewModel) {
            viewModel = viewModelProxy.createViewModel();
        }
        return viewModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelProxy = new ViewModelProxyImpl<>(this);
        baseView.initialize(savedInstanceState);
    }

    @Override
    public VM ensureViewModelCreated() {
        BaseActivity.this.viewModel = viewModelProxy.createViewModel();
        return BaseActivity.this.viewModel;
    }

    @Override
    public void initView() {
        //页面数据初始化方法
        BaseActivity.this.initData();
    }

    @Override
    public void refreshView(){

    }

    @Override
    public V getBinding(Bundle savedInstanceState) {
        BaseActivity.this.binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
        return BaseActivity.this.binding;
    }

    @Override
    public FragmentActivity FragmentActivity() {
        return BaseActivity.this;
    }

    @Override
    public Context getViewContext() {
        return BaseActivity.this;
    }

    @Override
    public LifecycleOwner getLifecycleOwner() {
        return BaseActivity.this;
    }

    @Override
    public Lifecycle getViewLifecycle() {
        return BaseActivity.this.getLifecycle();
    }

    @Override
    public boolean isFragment() {
        return false;
    }

    @Override
    public Fragment getFragment() {
        return null;
    }

    @Override
    public FragmentActivity getCurrentActivity() {
        return this;
    }

    @Override
    public LifecycleProvider<ActivityEvent> getLifecycleProvider() {
        return BaseActivity.this;
    }

    @Override
    public boolean isSwipeBack() {
        return false;
    }

    @Override
    public boolean customDialog() {
        return false;
    }

    @Override
    public boolean mvvmDialog() {
        return false;
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    @Override
    public boolean isRegisterEventBus() {
        return false;
    }

    /**
     * 是否注册广播
     */
    protected boolean isReceiver() {
        return false;
    }

    /**
     * 接收广播
     */
    public void onReceive(Intent intent) {
    }

    @Override
    protected void onDestroy() {
        KLog.i(this.getLocalClassName() + " : onDestroy()");
        super.onDestroy();
        baseView.onDestroy();
    }

    @Override
    public void requestCallPhone(boolean denied) {
    }

    @Override
    public boolean needReload() {
        return false;
    }

    @Override
    public void loadView() {

    }

    public void showDialog() {
        baseView.showDialog();
    }

    public void showDialog(String title) {
        baseView.showDialog(title);
    }

    @Override
    public void showCustomDialog() {
    }

    public void dismissDialog() {
        baseView.dismissDialog();
    }

    @Override
    public void dismissCustomDialog() {
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        baseView.startActivity(clz);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        baseView.startActivity(clz, bundle);
    }

    public void startActivityForResult(Class<?> clz, int requestCode, Bundle bundle) {
        baseView.startActivityForResult(clz, requestCode, bundle);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        baseView.startContainerActivity(canonicalName);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        baseView.startContainerActivity(canonicalName, bundle);
    }

    /**
     * =====================================================================
     **/
    @Override
    public void initParam() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public void getWifiRssi(int rssi) {

    }

    public void requestPermission(IPermission iPermission, String... permissions) {
        baseView.requestPermission(iPermission, permissions);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(MessageEvent event) {
        if (event != null && viewModel != null) {
            viewModel.receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(MessageEvent event) {
        if (event != null && viewModel != null) {
            viewModel.receiveStickyEvent(event);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.i(this.getLocalClassName() + " : onResume()");
    }

    @Override
    protected void onPause() {
        KLog.i(this.getLocalClassName() + " : onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        KLog.i(this.getLocalClassName() + " : onStop()");
        super.onStop();
    }
}
