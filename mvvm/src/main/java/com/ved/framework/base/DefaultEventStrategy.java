package com.ved.framework.base;

import com.ved.framework.bus.RxBus;
import com.ved.framework.bus.RxSubscriptions;
import com.ved.framework.bus.event.eventbus.MessageEvent;

import io.reactivex.rxjava3.disposables.Disposable;

public class DefaultEventStrategy implements IEventSubscriptionStrategy {
    private Disposable eventSubscription;

    @Override
    public void setupSubscription(BaseViewModel<?> viewModel) {
        eventSubscription = RxBus.getDefault()
                .toObservable(MessageEvent.class)
                .subscribe(viewModel::onEvent, viewModel::onError);
        RxSubscriptions.add(eventSubscription);
    }

    @Override
    public void remove() {
        if (eventSubscription != null && !eventSubscription.isDisposed()) {
            RxSubscriptions.remove(eventSubscription);
            eventSubscription = null;
        }
    }
}
