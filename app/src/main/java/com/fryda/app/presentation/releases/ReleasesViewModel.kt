package com.fryda.app.presentation.releases

import com.fryda.app.domain.use_cases.FridaReleaseUseCases
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fryda.app.core.constants.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val useCases: FridaReleaseUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReleasesUiState>(ReleasesUiState.Loading)
    val uiState: StateFlow<ReleasesUiState> = _uiState.asStateFlow()

    init {
        loadReleases()
    }

    fun loadReleases() {
        viewModelScope.launch {
            _uiState.value = ReleasesUiState.Loading

            when (val result = useCases.getReleases(page = 1, perPage = 30)) {
                is Result.Success -> {
                    _uiState.value = ReleasesUiState.Success(result.data)
                }

                is Result.Error -> {
                    _uiState.value = ReleasesUiState.Error(
                        result.errorMessage() ?: "An unknown error occurred"
                    )
                }

                else -> {}
            }
        }
    }
}