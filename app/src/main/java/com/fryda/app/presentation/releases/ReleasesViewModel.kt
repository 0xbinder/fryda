package com.fryda.app.presentation.releases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fryda.app.core.constants.Constants
import com.fryda.app.core.constants.Result
import com.fryda.app.domain.use_cases.FridaReleaseUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val useCases: FridaReleaseUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(ReleasesState())
    val state: StateFlow<ReleasesState> = _state.asStateFlow()

    init {
        loadReleases(isRefresh = false)
    }

    fun loadReleases(isRefresh: Boolean = false) {
        if (_state.value.isLoading || _state.value.isPaginating) return
        if (!isRefresh && _state.value.endReached) return

        viewModelScope.launch {
            if (isRefresh) {
                _state.update { it.copy(isRefreshing = true, currentPage = Constants.PAGE, endReached = false) }
            } else if (_state.value.releases.isEmpty()) {
                _state.update { it.copy(isLoading = true) }
            } else {
                _state.update { it.copy(isPaginating = true) }
            }

            val pageToLoad = if (isRefresh) 1 else _state.value.currentPage

            when (val result = useCases.getReleases(page = pageToLoad, perPage = 15)) {
                is Result.Success -> {
                    val newReleases = result.data
                    _state.update { current ->
                        current.copy(
                            releases = if (isRefresh) newReleases else current.releases + newReleases,
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            currentPage = pageToLoad + 1,
                            endReached = newReleases.isEmpty() || newReleases.size < 15,
                            error = null
                        )
                    }
                }

                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isPaginating = false,
                            error = result.errorMessage()
                        )
                    }
                }

                else -> {}
            }
        }
    }
}