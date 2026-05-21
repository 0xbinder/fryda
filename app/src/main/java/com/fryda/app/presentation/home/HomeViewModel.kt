// presentation/home/HomeViewModel.kt
package com.fryda.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fryda.app.core.constants.Result
import com.fryda.app.domain.use_cases.root_use_cases.RootUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val rootUseCases: RootUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        checkRoot()
    }

    fun checkRoot() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = rootUseCases.checkRoot()) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            rootStatus = result.data,
                            error = null
                        )
                    }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.errorMessage() ?: "Failed to check root status",
                            rootStatus = null
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun refresh() {
        checkRoot()
    }
}