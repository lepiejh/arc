package com.ved.framework.base

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.trello.rxlifecycle4.LifecycleProvider
import com.ved.framework.bus.RxBus
import com.ved.framework.bus.event.eventbus.EventBusUtil
import com.ved.framework.bus.event.eventbus.MessageEvent
import com.ved.framework.permission.IPermission
import com.ved.framework.utils.KLog
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by ved on 2017/6/15.
 */
open class BaseViewModel<M : BaseModel?> @JvmOverloads constructor(
    application: Application,
    private var model: M? = null
) : AndroidViewModel(
    application
), IBaseViewModel, Consumer<Disposable> {
    //弱引用持有
    private var lifecycle: WeakReference<LifecycleProvider<*>>? = null

    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private var mCompositeDisposable: CompositeDisposable?
    private val command: UICommand
    private var eventStrategy: IEventSubscriptionStrategy? = null
    private val backgroundJobs = ConcurrentHashMap<String, Job>()

    init {
        mCompositeDisposable = CompositeDisposable()
        command = UICommand()
        initEventStrategy()
    }

    private fun addSubscribe(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    override fun openEventSubscription(): Boolean {
        return false
    }

    override fun onEventSticky(): Boolean {
        return false
    }

    /**
     * 注入RxLifecycle生命周期
     */
    fun injectLifecycleProvider(lifecycle: LifecycleProvider<*>) {
        this.lifecycle = WeakReference(lifecycle)
    }

    fun getLifecycleProvider() = lifecycle?.get()

    fun getUC(): UIChangeLiveData = command.liveData

    fun showDialog() {
        command.showDialog()
    }

    fun showDialog(title: String?) {
        command.showDialog(title)
    }

    fun dismissDialog() {
        command.dismissDialog()
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>?) {
        command.startActivity(clz)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        command.startActivity(clz, bundle)
    }

    fun sendReceiver() {
        command.sendReceiver()
    }

    fun sendReceiver(bundle: Bundle?) {
        command.sendReceiver(bundle)
    }

    fun startActivityForResult(clz: Class<*>?, requestCode: Int) {
        command.startActivityForResult(clz, requestCode)
    }

    fun startActivityForResult(clz: Class<*>?, bundle: Bundle?, requestCode: Int) {
        command.startActivityForResult(clz, bundle, requestCode)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    fun startContainerActivity(canonicalName: String?) {
        command.startContainerActivity(canonicalName)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun startContainerActivity(canonicalName: String?, bundle: Bundle?) {
        command.startContainerActivity(canonicalName, bundle)
    }

    fun requestPermissions(iPermission: IPermission?, vararg permissions: String?) {
        command.requestPermissions(iPermission, *permissions)
    }

    fun callPhone(phoneNumber: String?) {
        command.callPhone(phoneNumber)
    }

    fun getWifiRssi(){
        command.getWifiRssi()
    }

    /**
     * 关闭界面
     */
    fun finish() {
        command.finish()
    }

    /**
     * 返回上一层
     */
    fun onBackPressed() {
        command.onBackPressed()
    }

    /**
     * 取消后台任务
     *
     * @param key 任务唯一标识，如果为null则取消所有任务
     * @param removeOnly 是否只从Map中移除而不实际取消任务(默认false)
     * @return Boolean 是否成功找到并取消了任务(当key为null时总是返回true)
     */
    fun cancelJob(key: String? = null, removeOnly: Boolean = false): Boolean {
        return when (key) {
            // 取消所有任务
            null -> {
                backgroundJobs.forEach { (_, job) ->
                    if (!removeOnly) job.cancel()
                }
                backgroundJobs.clear()
                true
            }
            // 取消指定key的任务
            else -> {
                val job = backgroundJobs[key]
                if (job != null) {
                    if (!removeOnly) job.cancel()
                    backgroundJobs.remove(key)
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * 线程切换 - 带键值管理
     */
    fun fetchWithCancel(
        key: String,
        ioAction: suspend CoroutineScope.() -> Unit = {},
        uiAction: suspend () -> Unit = {},
        onError: (Throwable) -> Unit = { KLog.e(it.message) },
        onCancel: (Throwable) -> Unit = { KLog.e(it.message) }
    ) {
        cancelJob(key) // 取消同key的旧任务

        backgroundJobs[key] = viewModelScope.launch {
            try {
                val ioJob = launch(Dispatchers.IO) { ioAction() }
                ioJob.join()
                withContext(Dispatchers.Main) { // 确保UI更新在主线程
                    uiAction()
                }
            } catch (e: CancellationException) {
                onCancel(e)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onError(e) }
            } finally {
                backgroundJobs.remove(key)
            }
        }
    }

    /**
     * 延时执行某个动作 - 带键值管理
     */
    fun delayedAction(
        key: String,
        delay: Long,
        block: () -> Unit
    ) {
        cancelJob(key) // 取消同key的旧任务

        backgroundJobs[key] = viewModelScope.launch {
            try {
                delay(delay)
                block()
            } finally {
                backgroundJobs.remove(key)
            }
        }
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {}
    override fun onCreate() {
        getUC().onLoadEvent.call()
    }

    private fun initEventStrategy() {
        eventStrategy = if (onEventSticky()) StickyEventStrategy() else DefaultEventStrategy()
    }

    fun sendRxEvent(messageEvent: MessageEvent<*>?) {
        if (onEventSticky()) {
            RxBus.getDefault().postSticky(messageEvent)
        } else {
            RxBus.getDefault().post(messageEvent)
        }
    }

    fun sendEvent(messageEvent: MessageEvent<*>?) {
        if (onEventSticky()) {
            EventBusUtil.sendStickyEvent(messageEvent)
        } else {
            EventBusUtil.sendEvent(messageEvent)
        }
    }

    private fun onStartEventSubscription() {
        eventStrategy?.setupSubscription(this)
    }

    override fun onEvent(event: MessageEvent<*>?) {}
    fun onError(throwable: Throwable?) {}
    override fun onDestroy() {}
    override fun onStart() {}
    override fun onStop() {}
    override fun onResume() {
        getUC().onResumeEvent.call()
    }

    override fun onPause() {}
    override fun registerRxBus() {
        if (openEventSubscription()) {
            onStartEventSubscription()
        }
    }

    override fun removeRxBus() {
        if (openEventSubscription()) {
            eventStrategy?.remove()
        }
    }

    override fun receiveEvent(event: MessageEvent<*>?) {}
    override fun receiveStickyEvent(event: MessageEvent<*>?) {}
    override fun onCleared() {
        super.onCleared()
        try {
            model?.onCleared()
            mCompositeDisposable?.clear()
            cancelJob()
            viewModelScope.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(Exception::class)
    override fun accept(disposable: Disposable) {
        addSubscribe(disposable)
    }
}