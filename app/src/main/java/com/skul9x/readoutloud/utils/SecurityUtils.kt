package com.skul9x.readoutloud.utils

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Generates a SHA-256 hash of the input string.
     * Returns the hex string representation.
     */
    fun sha256(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Generates a unique identifier for a (Model, API Key) pair.
     * Format: model::SHA256(apiKey)
     */
    fun getPairHash(model: String, apiKey: String): String {
        val keyHash = sha256(apiKey)
        return "$model::$keyHash"
    }
}
