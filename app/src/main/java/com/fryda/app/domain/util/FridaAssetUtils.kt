package com.fryda.app.domain.util

object FridaAssetUtils {

    fun isFridaServerAsset(name: String): Boolean {
        return name.startsWith("frida-server-") &&
                name.contains("-android-") &&
                name.endsWith(".xz")
    }

    fun archFromAssetName(name: String): String {
        return name.removePrefix("frida-server-")
            .substringAfterLast("-android-")
            .removeSuffix(".xz")
    }
}