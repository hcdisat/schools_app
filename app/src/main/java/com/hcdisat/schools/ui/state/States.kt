package com.hcdisat.schools.ui.state

sealed interface RequestState {
    object LOADING: RequestState
    data class SUCCESS<out T>(val results: T) : RequestState
    data class ERROR(val throwable: Throwable) : RequestState
}