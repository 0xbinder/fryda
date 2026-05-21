package com.fryda.app.domain.repository

import com.fryda.app.core.constants.Constants
import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.FridaRelease

interface FridaReleaseRepository {
    suspend fun getReleases(
        page: Int = Constants.PAGE,
        perPage: Int = Constants.PER_PAGE
    ): Result<List<FridaRelease>>

    suspend fun getRelease(version: String): Result<FridaRelease>
}