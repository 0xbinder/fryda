package com.fryda.app.domain.repository

import com.fryda.app.core.constants.Result
import com.fryda.app.domain.model.FridaRelease

interface FridaReleaseRepository {
    suspend fun getReleases(page: Int = 1, perPage: Int = 30): Result<List<FridaRelease>>
    suspend fun getRelease(version: String): Result<FridaRelease>
}