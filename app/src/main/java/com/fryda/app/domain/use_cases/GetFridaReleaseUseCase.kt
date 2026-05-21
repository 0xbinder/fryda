package com.fryda.app.domain.use_cases

import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.domain.repository.FridaReleaseRepository
import javax.inject.Inject

class GetFridaReleaseUseCase @Inject constructor(
    private val repository: FridaReleaseRepository
) {
    suspend operator fun invoke(version: String): Result<FridaRelease> {
        if (version.isBlank()) {
            return Result.Error("Version cannot be empty")
        }
        return repository.getRelease(version)
    }
}