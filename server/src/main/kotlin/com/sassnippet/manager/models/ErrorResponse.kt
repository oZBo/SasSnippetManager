package com.sassnippet.manager.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String
)
