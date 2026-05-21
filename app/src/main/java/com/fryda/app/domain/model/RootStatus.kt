package com.fryda.app.domain.model

data class RootStatus(
    val isRooted: Boolean,
    val architecture: String,
    val androidVersion: String,
    val suBinaryPath: String? = null,
    val magiskVersion: String? = null,
    val kernelSuPresent: Boolean = false,
    val apatchPresent: Boolean = false,
)
