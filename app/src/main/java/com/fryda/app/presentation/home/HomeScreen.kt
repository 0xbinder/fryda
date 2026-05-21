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
import com.fryda.app.presentation.home.components.BottomIconPill
import com.fryda.app.presentation.home.components.PrimaryActionButton
import com.fryda.app.presentation.home.components.TerminalInfoCard
import com.fryda.app.presentation.theme.ActionCardBg
import com.fryda.app.presentation.theme.HybridGradEnd
import com.fryda.app.presentation.theme.HybridGradMiddle
import com.fryda.app.presentation.theme.HybridGradStart
import com.fryda.app.presentation.theme.TerminalCardBg
import com.fryda.app.presentation.theme.TerminalText

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToLogs: () -> Unit,
    onNavigateToReleases: () -> Unit,
    onNavigateToServers: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uiState = state.toUiState()
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

                    Text(
                        text = "fryda",
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