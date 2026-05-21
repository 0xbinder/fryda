package com.fryda.app.domain.util

import android.os.Build

enum class DeviceArch(val label: String, val fridaSuffix: String) {
    ARM64  ("arm64-v8a",  "arm64"),
    ARM    ("armeabi-v7a","arm"),
    X86_64 ("x86_64",    "x86_64"),
    X86    ("x86",       "x86"),
    UNKNOWN("unknown",   "");

    companion object {
        fun current(): DeviceArch {
            val primary = Build.SUPPORTED_ABIS.firstOrNull() ?: return UNKNOWN
            return entries.firstOrNull { it.label == primary } ?: UNKNOWN
        }

        fun fromFridaSuffix(suffix: String) =
            entries.firstOrNull { it.fridaSuffix == suffix } ?: UNKNOWN
    }
}
