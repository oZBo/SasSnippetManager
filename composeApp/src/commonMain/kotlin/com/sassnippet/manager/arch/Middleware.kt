package com.sassnippet.manager.arch

/**
 * Handles side effects and dispatches new Intents.
 * Must call next() to continue the chain (or dispatch a different Intent).
 */
fun interface Middleware<S: Any, I : Any> {
    suspend fun process(intent: I, state: S, next: suspend (I) -> Unit)
}