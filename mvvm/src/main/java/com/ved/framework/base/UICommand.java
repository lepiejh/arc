package com.ved.framework.base;

import android.os.Bundle;

import com.orhanobut.dialog.navigation.ActivityCommandBuilder;
import com.ved.framework.permission.IPermission;
import com.ved.framework.utils.Constant;

import java.util.HashMap;
import java.util.Map;

public class UICommand {
    private final UIChangeLiveData liveData = new UIChangeLiveData();

    public void showDialog() {
        showDialog("请稍后...");
    }

    public void showDialog(String title) {
        liveData.getShowDialogEvent().postValue(title);
    }

    public void dismissDialog() {
        liveData.getDismissDialogEvent().call();
    }

    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    public void startActivity(Class<?> clz, Bundle bundle) {
        ActivityCommandBuilder.create()
                .setTarget(clz)
                .setBundle(bundle)
                .execute(liveData.getStartActivityEvent());
    }

    public void startActivityForResult(Class<?> clz, int requestCode) {
        startActivityForResult(clz, null, requestCode);
    }

    public void startActivityForResult(Class<?> clz, Bundle bundle, int requestCode) {
        ActivityCommandBuilder.create()
                .setTarget(clz)
                .setBundle(bundle)
                .setRequestCode(requestCode)
                .execute(liveData.getStartActivityForResultEvent());
    }

    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    public void startContainerActivity(String canonicalName, Bundle bundle) {
        ActivityCommandBuilder.create()
                .setCanonicalName(canonicalName)
                .setBundle(bundle)
                .execute(liveData.getStartContainerActivityEvent());
    }

    public void requestPermissions(IPermission iPermission, String... permissions) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PERMISSION, iPermission);
        params.put(Constant.PERMISSION_NAME, permissions);
        liveData.getRequestPermissionEvent().postValue(params);
    }

    public void callPhone(String phoneNumber) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PHONE_NUMBER, phoneNumber);
        liveData.getRequestCallPhoneEvent().postValue(params);
    }

    public void getWifiRssi(){
        liveData.getRequestWifiRssiEvent().call();
    }

    public void sendReceiver() {
        sendReceiver(null);
    }

    public void sendReceiver(Bundle bundle) {
        liveData.getReceiverEvent().postValue(bundle);
    }

    public void finish() {
        liveData.getFinishEvent().call();
    }

    public void onBackPressed() {
        liveData.getOnBackPressedEvent().call();
    }

    public UIChangeLiveData getLiveData() {
        return liveData;
    }
}
