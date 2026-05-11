package com.skul9x.readoutloud.data

import kotlinx.serialization.Serializable

@Serializable
data class ModelItem(
    val name: String,
    val isEnabled: Boolean = true
)
