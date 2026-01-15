package com.ved.framework.base;

import android.os.Bundle;

import com.ved.framework.bus.event.SingleLiveEvent;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public final class UIChangeLiveData extends SingleLiveEvent {
    private SingleLiveEvent<String> showDialogEvent;
    private SingleLiveEvent<Void> dismissDialogEvent;
    private SingleLiveEvent<Map<String, Object>> startActivityEvent;
    private SingleLiveEvent<Bundle> sendReceiverEvent;
    private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;
    private SingleLiveEvent<Map<String, Object>> startActivityForResultEvent;
    private SingleLiveEvent<Void> finishEvent;
    private SingleLiveEvent<Void> onBackPressedEvent;
    private SingleLiveEvent<Void> onLoadEvent;
    private SingleLiveEvent<Void> onResumeEvent;
    private SingleLiveEvent<Map<String, Object>> requestPermissionEvent;
    private SingleLiveEvent<Map<String, Object>> requestCallPhoneEvent;
    private SingleLiveEvent<Map<String, Object>> requestWifiRssiEvent;

    public SingleLiveEvent<Map<String, Object>> getRequestCallPhoneEvent() {
        return requestCallPhoneEvent = createLiveData(requestCallPhoneEvent);
    }

    public SingleLiveEvent<Map<String, Object>> getRequestWifiRssiEvent() {
        return requestWifiRssiEvent = createLiveData(requestWifiRssiEvent);
    }

    public SingleLiveEvent<Map<String, Object>> getRequestPermissionEvent() {
        return requestPermissionEvent = createLiveData(requestPermissionEvent);
    }

    public SingleLiveEvent<Map<String, Object>> getStartActivityForResultEvent() {
        return startActivityForResultEvent = createLiveData(startActivityForResultEvent);
    }

    public SingleLiveEvent<String> getShowDialogEvent() {
        return showDialogEvent = createLiveData(showDialogEvent);
    }

    public SingleLiveEvent<Void> getDismissDialogEvent() {
        return dismissDialogEvent = createLiveData(dismissDialogEvent);
    }

    public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
        return startActivityEvent = createLiveData(startActivityEvent);
    }

    public SingleLiveEvent<Bundle> getReceiverEvent() {
        return sendReceiverEvent = createLiveData(sendReceiverEvent);
    }

    public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
        return startContainerActivityEvent = createLiveData(startContainerActivityEvent);
    }

    public SingleLiveEvent<Void> getFinishEvent() {
        return finishEvent = createLiveData(finishEvent);
    }

    public SingleLiveEvent<Void> getOnBackPressedEvent() {
        return onBackPressedEvent = createLiveData(onBackPressedEvent);
    }

    public SingleLiveEvent<Void> getOnLoadEvent() {
        return onLoadEvent = createLiveData(onLoadEvent);
    }

    public SingleLiveEvent<Void> getOnResumeEvent() {
        return onResumeEvent = createLiveData(onResumeEvent);
    }

    private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
        if (liveData == null) {
            liveData = new SingleLiveEvent<>();
        }
        return liveData;
    }

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer observer) {
        super.observe(owner, observer);
    }
}
