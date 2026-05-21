package com.fryda.app.domain.use_cases.root_use_cases

import com.fryda.app.core.constants.Result
import com.fryda.app.domain.repository.RootRepository
import javax.inject.Inject

class IsRootedUseCase @Inject constructor(
    private val repository: RootRepository
) {
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            Result.Success(repository.isRooted())
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }
}