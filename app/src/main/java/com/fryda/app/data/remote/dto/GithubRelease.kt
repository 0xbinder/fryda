package com.fryda.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GithubRelease(
    @SerializedName("tag_name")     val tagName: String,
    @SerializedName("name")         val name: String,
    @SerializedName("published_at") val publishedAt: String,
    @SerializedName("assets")       val assets: List<GithubAsset>,
    @SerializedName("prerelease")   val preRelease: Boolean,
    @SerializedName("draft")        val draft: Boolean,
)