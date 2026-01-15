package com.ved.framework.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

public class ViewModelProxyImpl<VM extends BaseViewModel> implements ViewModelProxy<VM> {
    private final Object obj;
    private VM viewModel;

    public ViewModelProxyImpl(Object obj) {
        this.obj = obj;
    }

    public <T extends ViewModel> T createViewModel(Object obj, Class<T> cls) {
        if (obj instanceof FragmentActivity){
            return ViewModelProviders.of((FragmentActivity) obj).get(cls);
        }else if (obj instanceof Fragment){
            return ViewModelProviders.of((Fragment) obj).get(cls);
        }
        return null;
    }

    @Override
    public VM createViewModel() {
        if (viewModel == null) {
            Class modelClass;
            Type type = obj.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(obj, modelClass);
            return viewModel;
        }
        return viewModel;
    }
}