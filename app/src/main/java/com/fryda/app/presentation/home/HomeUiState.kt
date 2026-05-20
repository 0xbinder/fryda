package com.fryda.app.presentation.home

import com.fryda.app.core.root.RootChecker

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val rootStatus: RootChecker.RootStatus) : HomeUiState
}
