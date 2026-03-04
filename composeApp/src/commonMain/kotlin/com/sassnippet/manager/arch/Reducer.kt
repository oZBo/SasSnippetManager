package com.sassnippet.manager.arch

/**
 * Pure function — no side effects allowed.
 * (State, Intent) -> State
 */
fun interface Reducer<S : Any, I : Any> {
    fun reduce(state: S, intent: I): S
}