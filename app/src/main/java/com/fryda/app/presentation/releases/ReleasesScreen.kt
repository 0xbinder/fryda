package com.fryda.app.presentation.releases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleasesScreen(
    viewModel: ReleasesViewModel = hiltViewModel(),
    onNavigateToReleaseDetails: (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val backgroundBrush = remember {
        Brush.linearGradient(colors = listOf(HybridGradStart, HybridGradMiddle, HybridGradEnd))
    }

    // Only load more if we are NOT searching
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.endReached && state.searchQuery.isBlank()) {
            viewModel.loadReleases()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. SCROLLABLE HEADER & SEARCH BAR
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(top = 40.dp, bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "releases",
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
                            Spacer(modifier = Modifier.height(24.dp))

                            // SEARCH BAR
                            SearchBar(
                                query = state.searchQuery,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onSearch = { viewModel.searchRelease() },
                                onClear = { viewModel.clearSearch() }
                            )
                        }
                    }

                    // --- CONTENT LOGIC ---
                    if (state.searchQuery.isNotBlank()) {
                        // SEARCH MODE
                        if (state.isSearching) {
                            item {
                                Spacer(Modifier.height(40.dp))
                                CircularProgressIndicator(color = Color.White)
                            }
                        } else if (state.searchError != null) {
                            item {
                                ErrorState(message = state.searchError!!, onRetry = { viewModel.searchRelease() })
                            }
                        } else if (state.searchResult != null) {
                            item {
                                SectionHeader("SEARCH RESULT")
                                ReleaseCard(
                                    release = state.searchResult!!,
                                    onClick = { onNavigateToReleaseDetails(state.searchResult!!.tagName) }
                                )
                            }
                        }
                    } else {
                        // NORMAL PAGINATED LIST MODE
                        if (state.error != null && state.releases.isEmpty()) {
                            item {
                                ErrorState(message = state.error!!, onRetry = { viewModel.loadReleases() })
                            }
                        }

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
                    }

                    item { Spacer(modifier = Modifier.height(48.dp)) }
                }

                // Initial Full Screen Loading Center (Only if list is empty and not searching)
                if (state.isLoading && state.searchQuery.isBlank()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }
        }
    }
}

// --- NEW COMPONENT: SEARCH BAR ---
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search version (16.3.3)",
                fontFamily = FontFamily.Monospace,
                color = DopaTextSecondary,
                fontSize = 14.sp
            )
        },
        textStyle = TextStyle(
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TerminalCardBg,
            unfocusedContainerColor = TerminalCardBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color(0xFFA5D6A7) // Terminal green cursor
        ),
        leadingIcon = {
            Icon(Icons.Rounded.Search, contentDescription = null, tint = DopaTextSecondary)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = DopaTextSecondary)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

// --- EXISTING COMPONENTS ---

@Composable
private fun ReleaseCard(release: FridaRelease, onClick: () -> Unit) {
    Surface(
        color = ActionCardBg,
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
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = TerminalCardBg
            ) {
                Icon(
                    Icons.Rounded.CloudDownload,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = if (release.isLatest) Color(0xFFA5D6A7) else Color.White
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
                    fontFamily = FontFamily.Monospace,
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
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Surface(
        color = TerminalCardBg,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
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