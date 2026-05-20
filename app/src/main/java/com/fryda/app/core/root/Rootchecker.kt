package com.fryda.app.core.root

import android.content.Context
import android.os.Build
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootChecker @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    data class RootStatus(
        val isRooted: Boolean,
        val architecture: String,
        val androidVersion: String,
        val suBinaryPath: String? = null,
        val magiskVersion: String? = null,
        val kernelSuPresent: Boolean = false,
        val apatchPresent: Boolean = false,
    )

    suspend fun checkRoot(): RootStatus = withContext(Dispatchers.IO) {
        val arch = Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"
        val androidVer = "Android ${Build.VERSION.RELEASE}"
        val shell = Shell.getShell()
        if (!shell.isRoot) return@withContext RootStatus(
            isRooted = false,
            architecture = arch,
            androidVersion = androidVer
        )

        val suPath = Shell.cmd("which su").exec().out.firstOrNull()
        val magiskVer = Shell.cmd("magisk -v").exec().out.firstOrNull()
        val ksuPresent = Shell.cmd("ksud -V").exec().isSuccess
        val apaPresent = Shell.cmd("apd -V").exec().isSuccess

        RootStatus(
            isRooted = true,
            architecture = arch,
            androidVersion = androidVer,
            suBinaryPath = suPath,
            magiskVersion = magiskVer,
            kernelSuPresent = ksuPresent,
            apatchPresent = apaPresent,
        )
    }

    suspend fun isRooted(): Boolean = withContext(Dispatchers.IO) {
        Shell.getShell().isRoot
    }
}