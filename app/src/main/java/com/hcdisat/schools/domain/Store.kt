package com.hcdisat.schools.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class Store<S : State, A : Action>(
    private val initialState: S,
    private val reducer: Reducer<S, A>
) {
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)

    val currentState: StateFlow<S> get() = _state

    fun dispatch(action: A) = _state.update {
        reducer.reduce(it, action)
    }
}