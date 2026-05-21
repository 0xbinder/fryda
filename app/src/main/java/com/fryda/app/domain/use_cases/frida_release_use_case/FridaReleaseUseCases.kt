package com.fryda.app.domain.use_cases.frida_release_use_case

import javax.inject.Inject

data class FridaReleaseUseCases @Inject constructor(
    val getReleases: GetFridaReleasesUseCase,
    val getRelease: GetFridaReleaseUseCase
)