package com.hcdisat.schools.ui.state

sealed interface RequestState {
    object INITIAL: RequestState
    data class SUCCESS<out T>(val results: T) : RequestState
    data class ERROR(val throwable: Throwable) : RequestState
}