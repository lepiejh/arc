package com.ved.framework.utils

import java.util.*

class TimerTaskHeartBeat{
    private var timerTask: TimerTask? = null
    private var timer: Timer? = null

    fun startTimer(period: Long,callBack: () -> Unit) {
        try {
            if (timer == null){
                timer = Timer()
            }
            if (timerTask == null) {
                timerTask = object : TimerTask() {
                    override fun run() {
                        try {
                            callBack.invoke()
                        } catch (e: Exception) {
                            KLog.e(e.message)
                        }
                    }
                }
            }
            timer?.schedule(timerTask, 0, period)
        } catch (e: Exception) {
            stopTimer()
        }
    }

    fun stopTimer() {
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }
}