package com.example.domain

sealed class ParseResult {
    data class Success(
        val measurement: Measurement,
    ) : ParseResult()

    data class Failure(
        val message: String,
        val reason: String,
    ) : ParseResult()
}
