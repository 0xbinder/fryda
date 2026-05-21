package com.fryda.app.data.mappers

import com.fryda.app.data.remote.dto.GithubRelease
import com.fryda.app.domain.model.FridaAsset
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.domain.util.DeviceArch
import com.fryda.app.domain.util.FridaAssetUtils

fun GithubRelease.toDomain(isLatest: Boolean): FridaRelease {
    val version = tagName

    val androidAssets = assets
        .filter { FridaAssetUtils.isFridaServerAsset(it.name) }
        .map { asset ->
            val archSuffix = FridaAssetUtils.archFromAssetName(asset.name)
            FridaAsset(
                name = asset.name,
                downloadUrl = asset.downloadUrl,
                size = asset.size,
                architecture = DeviceArch.fromFridaSuffix(archSuffix),
            )
        }
        .filter { it.architecture != DeviceArch.UNKNOWN }

    return FridaRelease(
        version     = version,
        tagName     = tagName,
        publishedAt = publishedAt,
        assets      = androidAssets,
        isLatest    = isLatest,
    )
}
