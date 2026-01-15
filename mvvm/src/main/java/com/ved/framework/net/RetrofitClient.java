package com.ved.framework.net;

import com.ved.framework.base.BaseViewModel;
import com.ved.framework.http.cookie.CookieJarImpl;
import com.ved.framework.http.cookie.store.PersistentCookieStore;
import com.ved.framework.http.interceptor.CacheInterceptor;
import com.ved.framework.utils.Configure;
import com.ved.framework.utils.Constant;
import com.ved.framework.utils.CorpseUtils;
import com.ved.framework.utils.KLog;
import com.ved.framework.utils.MyGson;
import com.ved.framework.utils.StringUtils;
import com.ved.framework.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.net.Proxy;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

class RetrofitClient {

    private RetrofitClient() {
        // 防止反射破坏单例
        if (RetrofitClient.getInstance() != null) {
            throw new IllegalStateException("u can't instantiate me...");
        }
    }

    private static class SingletonHolder {
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static RetrofitClient getInstance() {
        return SingletonHolder.INSTANCE;
    }

    //防止反序列化产生多个对象
    private Object readResolve() throws ObjectStreamException {
        return RetrofitClient.getInstance();
    }

    public <T> T create(final Class<T> service, int i, Map<String, String> headers, IResult iResult, @Nullable BaseViewModel viewModel, @Nullable IResponse<?> iResponse) {
        if (service == null) {
            throw new RuntimeException("Api service is null!");
        }
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
        return new Retrofit.Builder()
                .client(RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())
                        .cache(new Cache(new File(Utils.getContext().getCacheDir(), "ved_cache"),Constant.CACHE_TIMEOUT))
                        .cookieJar(new CookieJarImpl(new PersistentCookieStore(Utils.getContext())))
                        .addInterceptor(new MyInterceptor(headers))
                        .addInterceptor(new CacheInterceptor(Utils.getContext()))
                        .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                        .addInterceptor(chain -> {
                            Request request = chain.request();
                            long startTime = System.currentTimeMillis();
                            Response response;
                            try {
                                response = chain.proceed(chain.request());
                            } catch (IOException e) {
                                if (viewModel != null) {
                                    viewModel.fetchWithCancel(CorpseUtils.INSTANCE.generateSecureRandomString(12),(coroutineScope, continuation) -> null, continuation -> {
                                        viewModel.dismissDialog();
                                        if (iResponse != null) iResponse.onError(e.getMessage(),e instanceof SocketException);
                                        return null;
                                    }, throwable -> null, throwable -> null);
                                }else {
                                    CorpseUtils.INSTANCE.handlerThread(() -> {
                                        if (iResponse != null) iResponse.onError(e.getMessage(),e instanceof SocketException);
                                        return null;
                                    });
                                }
                                throw e; // 继续抛出，让 RxJava 的 onError 处理
                            }
                            long endTime = System.currentTimeMillis();
                            long duration = endTime - startTime;
                            MediaType mediaType = response.body().contentType();
                            String content = response.body().string();
                            CorpseUtils.INSTANCE.inspectRequestBody(request);
                            KLog.e("Interceptor", "请求体返回：| Response:" + content);
                            KLog.e("Interceptor", "----------请求耗时:" + duration + "毫秒----------");
                            if (StringUtils.isNotEmpty(content)) {
                                try {
                                    JSONObject jsonObject = new JSONObject(content);
                                    int code = 0;
                                    String message = null;
                                    if (CorpseUtils.INSTANCE.isStandardJson(content)) {
                                        code = jsonObject.optInt("code");
                                        message = jsonObject.optString("msg");
                                    }else {
                                        code = jsonObject.optInt("status");
                                        message = jsonObject.optString("message");
                                    }
                                    iResult.onInfoResult(message,code);
                                }catch (Exception e)
                                {
                                    KLog.e(e.getMessage());
                                }
                            }
                            return response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
                        }).addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                        .connectTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(Constant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(8, 15, TimeUnit.SECONDS))
                        .proxy(Proxy.NO_PROXY)
                        .build())
                .addConverterFactory(GsonDConverterFactory.create(MyGson.getInstance().getGson()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(Configure.getUrl().get(i))
                .build().create(service);
    }

    public static <T> T execute(Observable<T> observable, Observer<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        return null;
    }

}

