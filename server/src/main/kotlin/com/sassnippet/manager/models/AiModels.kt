package com.sassnippet.manager.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Incoming request from the client
@Serializable
data class ConvertRequest(val code: String)

// Outgoing response to the client
@Serializable
data class ConvertResponse(val rCode: String)

// Groq API request/response models
@Serializable
data class GroqMessage(val role: String, val content: String)

@Serializable
data class GroqRequest(
    val model: String,
    val messages: List<GroqMessage>,
    @SerialName("max_tokens") val maxTokens: Int = 2048
)

@Serializable
data class GroqChoice(val message: GroqMessage)

@Serializable
data class GroqResponse(val choices: List<GroqChoice>)
