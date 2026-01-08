package com.onthecrow.nomadrates.uicore

interface Reducer<S: State, E: Event> {
    fun reduce(state: S, event: E): S
}
