package com.ved.framework.base;

public interface ViewModelProxy<VM extends BaseViewModel>{
    VM createViewModel();
}
