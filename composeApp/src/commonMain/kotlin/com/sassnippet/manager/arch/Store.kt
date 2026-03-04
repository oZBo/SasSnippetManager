package com.sassnippet.manager.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Generic Redux Store.
 *
 * Flow:
 *   UI dispatches Intent
 *     → Middleware chain handles side effects (API, EventBus, logging)
 *     → Middleware calls next() with the same or a new internal Intent
 *     → Reducer applies pure state transition
 *     → UI reacts to new State
 */
class Store<S : Any, I : Any>(
    initialState: S,
    private val reducer: Reducer<S, I>,
    private val middlewares: List<Middleware<S, I>> = emptyList(),
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    fun dispatch(intent: I) {
        scope.launch {
            execute(intent)
        }
    }

    private suspend fun execute(intent: I) {
        // Build a suspend chain — must be inside suspend fun to be valid
        val chain: suspend (I) -> Unit = middlewares.foldRight(
            { i: I -> applyReducer(i) }
        ) { middleware, next ->
            { i: I -> middleware.process(i, _state.value, next) }
        }
        chain(intent)
    }

    private fun applyReducer(intent: I) {
        _state.value = reducer.reduce(_state.value, intent)
    }
}