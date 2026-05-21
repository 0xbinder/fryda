package com.fryda.app.domain.use_cases.root_use_cases

import javax.inject.Inject

data class RootUseCases @Inject constructor(
    val checkRoot: CheckRootUseCase,
    val isRooted: IsRootedUseCase
)