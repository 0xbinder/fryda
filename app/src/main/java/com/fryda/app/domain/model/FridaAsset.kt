package com.fryda.app.domain.model

import com.fryda.app.domain.util.DeviceArch

data class FridaAsset(
    val name: String,
    val downloadUrl: String,
    val size: Long,
    val architecture: DeviceArch,
)
