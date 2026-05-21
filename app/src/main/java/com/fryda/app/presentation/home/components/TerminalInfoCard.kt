package com.fryda.app.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fryda.app.presentation.home.HomeUiState
import com.fryda.app.presentation.theme.TerminalCardBg

@Composable
 fun TerminalInfoCard(uiState: HomeUiState) {
    Surface(
        color = TerminalCardBg,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Welcome to fryda manager",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            when (uiState) {
                is HomeUiState.Loading -> {
                    TerminalTextLine("Initializing environment...")
                    TerminalTextLine("Checking root privileges...")
                }
                is HomeUiState.Success -> {
                    val status = uiState.rootStatus

                    TerminalTextLine("OS Version: ${status.androidVersion}")
                    TerminalTextLine("Architecture: ${status.architecture}")

                    val rootEnv = when {
                        status.kernelSuPresent -> "KernelSU"
                        status.apatchPresent -> "APatch"
                        status.magiskVersion != null -> "Magisk ${status.magiskVersion.substringBefore(":")}"
                        else -> "None"
                    }
                    TerminalTextLine("Environment: $rootEnv")

                    if (status.isRooted && !status.suBinaryPath.isNullOrBlank()) {
                        TerminalTextLine("Path: ${status.suBinaryPath}")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (status.isRooted) "Status: Privileged" else "Status: Rootless / Restricted",
                        color = if (status.isRooted) Color(0xFFA5D6A7) else Color(0xFFEF9A9A),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                else -> {}
            }
        }
    }
}