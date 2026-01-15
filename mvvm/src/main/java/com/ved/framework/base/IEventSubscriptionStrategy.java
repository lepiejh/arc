package com.ved.framework.base;

public interface IEventSubscriptionStrategy {
    void setupSubscription(BaseViewModel<?> viewModel);

    void remove();
}
