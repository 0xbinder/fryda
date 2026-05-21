package com.fryda.app.presentation.releases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.presentation.theme.* // Make sure to import your theme colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleasesScreen(
    viewModel: ReleasesViewModel = hiltViewModel(),
    onNavigateToReleaseDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Complex diagonal gradient (Teal -> Slate -> Olive) to match Home
    val backgroundBrush = remember {
        Brush.linearGradient(
            colors = listOf(HybridGradStart, HybridGradMiddle, HybridGradEnd)
        )
    }

    // Detect when we reach the end of the list to load more
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.endReached) {
            viewModel.loadReleases()
        }
    }

    Scaffold(
        containerColor = Color.Transparent, // Let the background brush show through
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Allows drawing behind system bars
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush) // Full screen gradient
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.loadReleases(isRefresh = true) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Centered vibe
                ) {
                    // 1. SCROLLABLE HEADER
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding() // Keeps text below the notch/camera
                                .padding(top = 40.dp, bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "releases", // Lowercase and bold to match home
                                fontSize = 42.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-1.5).sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "official frida distribution",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = DopaTextSecondary
                            )
                        }
                    }

                    // 2. ERROR STATE
                    if (state.error != null && state.releases.isEmpty()) {
                        item {
                            ErrorState(message = state.error!!, onRetry = { viewModel.loadReleases() })
                        }
                    }

                    // 3. RELEASES LIST
                    itemsIndexed(
                        items = state.releases,
                        key = { _, release -> release.tagName }
                    ) { index, release ->
                        if (index == 0) {
                            SectionHeader("LATEST VERSION")
                        } else if (index == 1) {
                            Spacer(modifier = Modifier.height(16.dp))
                            SectionHeader("PREVIOUS RELEASES")
                        }

                        ReleaseCard(
                            release = release,
                            onClick = { onNavigateToReleaseDetails(release.tagName) }
                        )
                    }

                    // 4. BOTTOM LOADING INDICATOR (Pagination)
                    if (state.isPaginating) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(48.dp)) }
                }

                // Initial Loading Center
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ReleaseCard(release: FridaRelease, onClick: () -> Unit) {
    Surface(
        color = ActionCardBg, // Translucent glassmorphism
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Background
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape, // Circular icon backgrounds look better here
                color = TerminalCardBg // Darker translucent
            ) {
                Icon(
                    Icons.Rounded.CloudDownload,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (release.isLatest) Color(0xFFA5D6A7) else Color.White // Highlight latest
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "v${release.version}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (release.isLatest) {
                        Spacer(Modifier.width(8.dp))
                        // Custom Glassmorphism Badge
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "LATEST",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${release.assets.size} assets • ${release.publishedAt.take(10)}",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace, // Keep the terminal vibe
                    color = DopaTextSecondary
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = DopaTextSecondary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center // Centered headers
    )
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Surface(
        color = TerminalCardBg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                color = Color(0xFFEF9A9A),
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = ActionCardBg,
                shape = CircleShape,
                modifier = Modifier.clip(CircleShape).clickable { onRetry() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}