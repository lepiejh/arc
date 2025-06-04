package com.donkor.cn.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import com.ved.framework.utils.Utils

class WifiSignalHelper private constructor() {
    private var wifiManager: WifiManager = Utils.getContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var receiver: BroadcastReceiver? = null
    private val handler = Handler(Looper.getMainLooper())
    private var pollingRunnable: Runnable? = null
    private var pollingInterval = 3000L

    companion object {
        val INSTANCE: WifiSignalHelper by lazy { Holder.INSTANCE }
    }

    private object Holder {
        val INSTANCE = WifiSignalHelper()
    }

    private fun startPolling(callback: (rssi: Int) -> Unit) {
        stopPolling()

        pollingRunnable = object : Runnable {
            override fun run() {
                val info = wifiManager.connectionInfo
                val rssi = info?.rssi ?: -100
                callback(rssi)
                handler.postDelayed(this, pollingInterval)
            }
        }
        handler.post(pollingRunnable!!)
    }

    private fun stopPolling() {
        pollingRunnable?.let {
            handler.removeCallbacks(it)
            pollingRunnable = null
        }
    }

    fun startListening(callback: (rssi: Int) -> Unit) {
        val initialRssi = wifiManager.connectionInfo?.rssi ?: -100
        callback(initialRssi)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiManager.RSSI_CHANGED_ACTION,
                    WifiManager.NETWORK_STATE_CHANGED_ACTION,
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                        val rssi = wifiManager.connectionInfo?.rssi ?: -100
                        callback(rssi)
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(WifiManager.RSSI_CHANGED_ACTION)
            addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
            addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        }

        Utils.getContext().registerReceiver(receiver, filter)

        startPolling(callback)
    }

    fun stopListening() {
        receiver?.let {
            Utils.getContext().unregisterReceiver(it)
            receiver = null
        }
        stopPolling()
    }
}