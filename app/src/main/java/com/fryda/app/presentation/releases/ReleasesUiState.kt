package com.fryda.app.presentation.releases

import com.fryda.app.domain.model.FridaRelease

sealed interface ReleasesUiState {
    data object Loading : ReleasesUiState
    data class Success(val releases: List<FridaRelease>) : ReleasesUiState
    data class Error(val message: String) : ReleasesUiState
}