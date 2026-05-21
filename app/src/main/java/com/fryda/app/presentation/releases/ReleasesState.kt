package com.fryda.app.presentation.releases

import com.fryda.app.domain.model.FridaRelease

data class ReleasesState(
    val releases: List<FridaRelease> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isPaginating: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val endReached: Boolean = false
)