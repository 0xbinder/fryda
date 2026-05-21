package com.fryda.app.presentation.home

data class HomeState(
    val isLoading: Boolean = false,
    val rootStatus: com.fryda.app.domain.model.RootStatus? = null,
    val error: String? = null
)