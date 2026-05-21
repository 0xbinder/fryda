package com.fryda.app.domain.use_cases

import javax.inject.Inject

data class FridaReleaseUseCases @Inject constructor(
    val getReleases: GetFridaReleasesUseCase,
    val getRelease: GetFridaReleaseUseCase
)