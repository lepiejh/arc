package com.ved.framework.net;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.ved.framework.base.BaseViewModel;
import com.ved.framework.http.ResponseThrowable;
import com.ved.framework.utils.Configure;
import com.ved.framework.utils.CorpseUtils;
import com.ved.framework.utils.KLog;
import com.ved.framework.utils.NetUtil;
import com.ved.framework.utils.RxUtils;
import com.ved.framework.utils.StringUtils;
import com.ved.framework.utils.Utils;

import java.util.Map;

import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.subjects.PublishSubject;

/**
 * 网络请求
 *
 * @param <T> service interface
 * @param <K> 返回的数据类型
 *
 * 使用生命周期事件流
 * val lifecycleDisposable = PublishSubject.create<Unit>()
 *
 * 当需要取消时
 * lifecycleDisposable.onNext(Unit) 取消网络请求后需要 延时 1 秒再重新请求 才能生效，可能是由于 请求取消后资源未完全释放 或 OkHttp 连接池未及时清理
 *
 */
public abstract class ARequest<T, K> {
    private Activity activity;
    private BaseViewModel viewModel;
    private Class<? extends T> service;
    private IMethod<T, K> method;
    private int index = 0;
    private boolean isLoading = false;
    private View viewState;
    private ISeatSuccess seatSuccess;
    private ISeatError seatError;
    private IResponse<K> response;
    private Map<String, String> headers;

    public ARequest<T, K> withActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public ARequest<T, K> withViewModel(BaseViewModel viewModel) {
        this.viewModel = viewModel;
        return this;
    }

    public ARequest<T, K> withService(Class<? extends T> service) {
        this.service = service;
        return this;
    }

    public ARequest<T, K> withMethod(IMethod<T, K> method) {
        this.method = method;
        return this;
    }

    public ARequest<T, K> withIndex(int index) {
        this.index = index;
        return this;
    }

    public ARequest<T, K> withLoading(boolean isLoading) {
        this.isLoading = isLoading;
        return this;
    }

    public ARequest<T, K> withViewState(View viewState) {
        this.viewState = viewState;
        return this;
    }

    public ARequest<T, K> withSeatSuccess(ISeatSuccess seatSuccess) {
        this.seatSuccess = seatSuccess;
        return this;
    }

    public ARequest<T, K> withSeatError(ISeatError seatError) {
        this.seatError = seatError;
        return this;
    }

    public ARequest<T, K> withResponse(IResponse<K> response) {
        this.response = response;
        return this;
    }

    public ARequest<T, K> withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public PublishSubject<Object> build(){
        return request(activity,viewModel,method,service,viewState,seatSuccess,seatError,headers,index,isLoading,response);
    }

    @SuppressLint("CheckResult")
    private PublishSubject<Object> request(@Nullable Activity activity, @Nullable BaseViewModel viewModel,
                                          @Nullable IMethod<T, K> method,@Nullable Class<? extends T> service,
                                          View view,ISeatSuccess seatSuccess,ISeatError seatError,Map<String, String> headers,
                                          int index,boolean isLoading, @Nullable IResponse<K> iResponse) {
        PublishSubject<Object> lifecycleDisposable = PublishSubject.create();
        if (NetUtil.getNetWorkStart(Utils.getContext()) == 1) {
            if (iResponse != null) {
                iResponse.onError("网络异常",false);
            }
            if (view != null && seatSuccess != null) {
                //手机无网络
                seatSuccess.onNoNetworkView();
            }
            exceptionHandling(activity, "网络异常", -1);
        } else {
            if (view!= null && seatSuccess != null) {
                seatSuccess.onStateView();
            }
            if (isLoading && viewModel != null) {
                viewModel.showDialog();
            }
            try {
                final String[] msg = new String[1];
                if (method != null) {
                    Observable o = method.method(RetrofitClient.getInstance().create(service, index, headers, (message, code) -> {
                        if (code!= Configure.getCode())
                        {
                            msg[0] =message;
                        }
                    },viewModel,iResponse));
                    if (viewModel != null) {
                        o.compose(RxUtils.bindToLifecycle(viewModel.getLifecycleProvider())); // 请求与View周期同步
                    }
                    o.compose(RxUtils.schedulersTransformer())
                            .compose(observable -> observable
                                    .onErrorResumeNext((Function<Throwable, ObservableSource>) throwable -> {
                                        KLog.e(throwable.getMessage());
                                        parseError(isLoading,viewModel,msg[0],view,seatError,iResponse,null,activity);
                                        return Observable.error(throwable);
                                    }))
                            .takeUntil(lifecycleDisposable)
                            .subscribe((Consumer<K>) response ->
                                            parseSuccess(viewModel,view, isLoading, iResponse, response),
                                    (Consumer<ResponseThrowable>) throwable ->
                                            parseError(isLoading,viewModel, null,view,seatError,iResponse, throwable, activity));
                }
            } catch (Exception e) {
                KLog.e(e.getMessage());
                if (viewModel != null) {
                    viewModel.fetchWithCancel(CorpseUtils.INSTANCE.generateSecureRandomString(12),(coroutineScope, continuation) -> null, continuation -> {
                        parseError(isLoading,viewModel,"连接服务器失败或其他异常",view,seatError,iResponse,null,activity);
                        return null;
                    }, throwable -> null, throwable -> null);
                }else {
                    CorpseUtils.INSTANCE.handlerThread(() -> {
                        parseError(isLoading, null,"连接服务器失败或其他异常",view,seatError,iResponse,null,activity);
                        return null;
                    });
                }
            }
        }
        return lifecycleDisposable;
    }

    private void parseSuccess(@Nullable BaseViewModel viewModel, View viewState,boolean isLoading, IResponse<K> iResponse, K response) {
        if (viewState!= null) {
            viewState.setVisibility(View.GONE);
        }
        if (isLoading && viewModel != null) {
            viewModel.dismissDialog();
        }
        if (iResponse != null) {
            iResponse.onSuccess(response);
        }
    }

    private void parseError(boolean isLoading,@Nullable BaseViewModel viewModel,String error,View viewState,
                            ISeatError seatError,IResponse<K> iResponse, ResponseThrowable throwable, Activity activity) {
        if (isLoading && viewModel!=null)
        {
            viewModel.dismissDialog();
        }
        if (viewState!= null && seatError != null) {
            seatError.onErrorView();
        }
        if (iResponse != null && StringUtils.isNotEmpty(error)) {
            iResponse.onError(error,false);
        }
        if (throwable != null) {
            KLog.e(throwable.message);
            if (throwable.getCause() instanceof ResultException)
            {
                ResultException resultException = (ResultException) throwable.getCause();
                exceptionHandling(activity, resultException.getErrMsg(), resultException.getErrCode());
                if (viewState!= null && seatError != null) {
                    seatError.onErrorView();
                    seatError.onErrorHandler(resultException.getErrCode());
                }

                if (TextUtils.isEmpty(resultException.getErrMsg())) {
                    if (iResponse != null) {
                        iResponse.onError(throwable.message,false);
                    }
                } else {
                    if (iResponse != null) {
                        iResponse.onError(resultException.getErrMsg(),false);
                    }
                }
                if (viewState!= null && seatError != null) {
                    seatError.onEmptyView();
                }
            } else {
                if (iResponse != null) {
                    iResponse.onError(throwable.message,false);
                }
                if (seatError != null) {
                    seatError.onEmptyView(throwable.message);
                }
            }
        }else {
            if (StringUtils.isNotEmpty(error)) {
                exceptionHandling(activity, error, -2);
            }
        }
    }

    public abstract void exceptionHandling(@Nullable Activity activity, @Nullable String error, int code);
}