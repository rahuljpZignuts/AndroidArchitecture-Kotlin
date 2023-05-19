package com.test.rahul.helper

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.test.rahul.utils.DateTimeUtils
import java.util.concurrent.TimeUnit

class Timer {

    private var countDownTimer: CountDownTimer? = null
    private var timerTime: Long? = null
    private var endTime: Long? = null

    private val _onConfigured = MutableLiveData<Int?>()
    val onConfigured: LiveData<Int?> = _onConfigured
    private val _onTick = MutableLiveData<Int?>()
    val onTick: LiveData<Int?> = _onTick
    private val _onFinish = MutableLiveData<Unit?>()
    val onFinish: LiveData<Unit?> = _onFinish

    private fun startInternal() {
        val resendTime = endTime?.let { it - DateTimeUtils.getCurrentTimeInMillis() } ?: TIME_IN_MS
        if (resendTime > 0) {
            countDownTimer = object : CountDownTimer(resendTime, TIME_INTERVAL) {
                override fun onTick(millisUntilFinished: Long) {
                    _onTick.value = (millisUntilFinished / TIME_INTERVAL).toInt()
                }

                override fun onFinish() {
                    _onFinish.postValue(Unit)
                    reset()
                }
            }
            countDownTimer?.start()
        } else {
            _onFinish.postValue(Unit)
            reset()
        }
    }

    fun start() {
        startInternal()
    }

    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun reset() {
        cancel()
        timerTime = null
        endTime = null
        _onConfigured.value = null
        _onTick.value = null
        _onFinish.value = null
    }

    fun set(time: Long) {
        reset()
        timerTime = time
        endTime = DateTimeUtils.getCurrentTimeInMillis() + TimeUnit.SECONDS.toMillis(time)
        _onConfigured.value = time.toInt()
    }

    companion object {
        private const val TIME_IN_MS = 60_000L
        private const val TIME_INTERVAL = 1_000L
    }
}
