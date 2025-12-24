package com.first.ahikarov.peaceofmind

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PeaceOfMindViewModel : ViewModel() {

    enum class BreathingState { IDLE, INHALE, HOLD_IN, EXHALE }

    private val _currentState = MutableLiveData<BreathingState>(BreathingState.IDLE)
    val currentState: LiveData<BreathingState> = _currentState

    private val _isActive = MutableLiveData<Boolean>(false)
    val isActive: LiveData<Boolean> = _isActive

    private val _cycleCount = MutableLiveData<Int>(0)
    val cycleCount: LiveData<Int> = _cycleCount

    private val _secondsElapsed = MutableLiveData<Int>(0)
    val secondsElapsed: LiveData<Int> = _secondsElapsed

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    fun toggleBreathing() {
        val nextStatus = !(_isActive.value ?: false)
        _isActive.value = nextStatus
        
        if (nextStatus) startTimer() else resetSession()
    }

    private fun resetSession() {
        stopTimer()
        _currentState.value = BreathingState.IDLE
        _cycleCount.value = 0
        _secondsElapsed.value = 0
    }

    private fun startTimer() {
        timerRunnable = object : Runnable {
            override fun run() {
                if (doIfActive { _secondsElapsed.value = (_secondsElapsed.value ?: 0) + 1 }) {
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.postDelayed(timerRunnable!!, 1000)
    }

    private fun stopTimer() {
        timerRunnable?.let { handler.removeCallbacks(it) }
    }

    fun updateState(state: BreathingState) = doIfActive { _currentState.value = state }

    fun incrementCycle() = doIfActive { _cycleCount.value = (_cycleCount.value ?: 0) + 1 }

    // פונקציית עזר למניעת חזרתיות על בדיקת isActive
    private inline fun doIfActive(action: () -> Unit): Boolean {
        return if (_isActive.value == true) {
            action()
            true
        } else false
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}