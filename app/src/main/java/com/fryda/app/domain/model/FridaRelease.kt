package com.fryda.app.domain.model

data class FridaRelease(
    val version: String,
    val tagName: String,
    val publishedAt: String,
    val assets: List<FridaAsset>,
    val isLatest: Boolean = false,
)
