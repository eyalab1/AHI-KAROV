package com.first.ahikarov.peaceofmind

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import java.util.Locale

class PeaceOfMindViewModel : ViewModel() {

    enum class BreathingState { IDLE, INHALE, HOLD_IN, EXHALE, HOLD_OUT }

    private val _currentState = MutableLiveData<BreathingState>(BreathingState.IDLE)
    val currentState: LiveData<BreathingState> = _currentState

    private val _isActive = MutableLiveData<Boolean>(false)
    val isActive: LiveData<Boolean> = _isActive

    private val _cycleCount = MutableLiveData<Int>(0)
    val formattedCycles: LiveData<String> = _cycleCount.map { count -> "cycle $count" }

    private val _secondsElapsed = MutableLiveData<Int>(0)
    val formattedTime: LiveData<String> = _secondsElapsed.map { totalSeconds ->
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    fun toggleBreathing() {
        val nextStatus = !(_isActive.value ?: false)
        _isActive.value = nextStatus
        
        if (nextStatus) {
            startTimer()
            _currentState.value = BreathingState.INHALE
        } else {
            resetSession()
        }
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

    fun onStepFinished() {
        if (_isActive.value != true) return
        
        val nextState = when (_currentState.value) {
            BreathingState.INHALE -> BreathingState.HOLD_IN
            BreathingState.HOLD_IN -> BreathingState.EXHALE
            BreathingState.EXHALE -> BreathingState.HOLD_OUT
            BreathingState.HOLD_OUT -> {
                _cycleCount.value = (_cycleCount.value ?: 0) + 1
                BreathingState.INHALE
            }
            else -> BreathingState.IDLE
        }
        _currentState.value = nextState
    }

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
