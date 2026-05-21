// presentation/home/HomeUiState.kt
package com.fryda.app.presentation.home

import com.fryda.app.domain.model.RootStatus

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val rootStatus: RootStatus) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

fun HomeState.toUiState(): HomeUiState {
    return when {
        isLoading -> HomeUiState.Loading
        error != null -> HomeUiState.Error(error)
        rootStatus != null -> HomeUiState.Success(rootStatus)
        else -> HomeUiState.Loading
    }
}