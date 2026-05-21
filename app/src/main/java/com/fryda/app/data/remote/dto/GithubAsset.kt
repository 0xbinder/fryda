package com.fryda.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GithubAsset(
    @SerializedName("name")                 val name: String,
    @SerializedName("browser_download_url") val downloadUrl: String,
    @SerializedName("size")                 val size: Long,
    @SerializedName("content_type")         val contentType: String,
)