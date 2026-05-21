package com.fryda.app.domain.use_cases.root_use_cases

import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.RootStatus
import com.fryda.app.domain.repository.RootRepository
import javax.inject.Inject

class CheckRootUseCase @Inject constructor(
    private val repository: RootRepository
) {
    suspend operator fun invoke(): Result<RootStatus> {
        return try {
            val status = repository.checkRoot()
            Result.Success(status)
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }
}