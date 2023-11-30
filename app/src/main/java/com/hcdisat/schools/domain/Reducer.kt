package com.hcdisat.schools.domain

interface Reducer<S: State, A: Action> {
    fun reduce(currentState: S, action: A): S
}