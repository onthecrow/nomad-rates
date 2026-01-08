package com.onthecrow.nomadrates.uicore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel<E : Event, S : State, R : Reducer<S, E>>(
    private val reducer: R
) : ViewModel() {

    private val _event: MutableSharedFlow<E> =
        MutableSharedFlow(extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val event = _event.asSharedFlow()

    private val _state: StateFlow<S> = _event.runningFold(getInitialState(), reducer::reduce)
        .stateIn(viewModelScope, SharingStarted.Eagerly, getInitialState())
    val state get() = _state

    abstract fun getInitialState(): S

    fun onEvent(event: E) {
        _event.tryEmit(event)
    }
}
