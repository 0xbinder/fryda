package com.fryda.app.presentation.releases

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fryda.app.presentation.releases.components.ErrorState
import com.fryda.app.presentation.releases.components.ReleaseCard
import com.fryda.app.presentation.releases.components.SearchBar
import com.fryda.app.presentation.releases.components.SectionHeader
import com.fryda.app.presentation.theme.DopaTextSecondary
import com.fryda.app.presentation.theme.HybridGradEnd
import com.fryda.app.presentation.theme.HybridGradMiddle
import com.fryda.app.presentation.theme.HybridGradStart

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

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem =
                listState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
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

                            SearchBar(
                                query = state.searchQuery,
                                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                                onSearch = { viewModel.searchRelease() },
                                onClear = { viewModel.clearSearch() }
                            )
                        }
                    }

                    if (state.searchQuery.isNotBlank()) {
                        if (state.isSearching) {
                            item {
                                Spacer(Modifier.height(40.dp))
                                CircularProgressIndicator(color = Color.White)
                            }
                        } else if (state.searchError != null) {
                            item {
                                ErrorState(
                                    message = state.searchError!!,
                                    onRetry = { viewModel.searchRelease() })
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
                        if (state.error != null && state.releases.isEmpty()) {
                            item {
                                ErrorState(
                                    message = state.error!!,
                                    onRetry = { viewModel.loadReleases() })
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
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
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