package com.fryda.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fryda.app.presentation.theme.ActionCardBg
import com.fryda.app.presentation.theme.HybridGradEnd
import com.fryda.app.presentation.theme.HybridGradMiddle
import com.fryda.app.presentation.theme.HybridGradStart
import com.fryda.app.presentation.theme.TerminalCardBg
import com.fryda.app.presentation.theme.TerminalText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToReleases: () -> Unit,
    onNavigateToServers: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing = uiState is HomeUiState.Loading
    val refreshState = rememberPullToRefreshState()

    val backgroundBrush = remember {
        Brush.linearGradient(
            colors = listOf(HybridGradStart, HybridGradMiddle, HybridGradEnd)
        )
    }

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.checkRoot() },
                state = refreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding() + 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Centered like palera1n
                ) {
                    Spacer(modifier = Modifier.height(60.dp))

                    // --- HEADER SECTION ---
                    Text(
                        text = "fryda", // Lowercase and bold
                        fontSize = 46.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.5).sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    TerminalInfoCard(uiState)

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryActionButton(
                        title = "Manage Frida Servers",
                        icon = Icons.Rounded.Terminal,
                        onClick = onNavigateToServers
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    PrimaryActionButton(
                        title = "Download Releases",
                        icon = Icons.Rounded.CloudDownload,
                        onClick = onNavigateToReleases
                    )

                    Spacer(modifier = Modifier.weight(1f, fill = true))
                    Spacer(modifier = Modifier.height(48.dp))

                    BottomIconPill(
                        onSettingsClick = onNavigateToSettings,
                        onLogsClick = onNavigateToLogs
                    )
                }
            }
        }
    }
}

@Composable
private fun TerminalInfoCard(uiState: HomeUiState) {
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
            }
        }
    }
}

@Composable
private fun TerminalTextLine(text: String) {
    Text(
        text = text,
        color = TerminalText,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}

@Composable
private fun PrimaryActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        color = ActionCardBg,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun BottomIconPill(
    onSettingsClick: () -> Unit,
    onLogsClick: () -> Unit
) {
    Surface(
        color = TerminalCardBg, // Matches the dark translucent feel
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = onLogsClick,
                modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Description,
                    contentDescription = "Logs",
                    tint = Color.White
                )
            }
        }
    }
}