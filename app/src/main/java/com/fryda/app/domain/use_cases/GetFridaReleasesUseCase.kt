package com.fryda.app.domain.use_cases

import com.fryda.app.core.constants.Constants
import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.domain.repository.FridaReleaseRepository
import javax.inject.Inject

class GetFridaReleasesUseCase @Inject constructor(
    private val repository: FridaReleaseRepository
) {
    suspend operator fun invoke(
        page: Int = Constants.PAGE,
        perPage: Int = Constants.PER_PAGE
    ): Result<List<FridaRelease>> {
        return repository.getReleases(page, perPage)
    }
}