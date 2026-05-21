package com.fryda.app.domain.use_cases

import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.domain.repository.FridaReleaseRepository
import javax.inject.Inject

class GetFridaReleasesUseCase @Inject constructor(
    private val repository: FridaReleaseRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 30
    ): Result<List<FridaRelease>> {
        return repository.getReleases(page, perPage)
    }
}