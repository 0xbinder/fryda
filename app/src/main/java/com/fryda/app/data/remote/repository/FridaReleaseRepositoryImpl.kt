package com.fryda.app.data.remote.repository

import com.fryda.app.core.constants.Result
import com.fryda.app.core.constants.safeApiCall
import com.fryda.app.data.mappers.toDomain
import com.fryda.app.data.remote.api.GithubApi
import com.fryda.app.domain.model.FridaRelease
import com.fryda.app.domain.repository.FridaReleaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FridaReleaseRepositoryImpl @Inject constructor(
    private val api: GithubApi
) : FridaReleaseRepository {
    override suspend fun getReleases(
        page: Int,
        perPage: Int
    ): Result<List<FridaRelease>> =
        safeApiCall {
            api.listReleases(
                page = page,
                perPage = perPage,
            ).filter { !it.draft && !it.preRelease }
                .map { it.toDomain(isLatest = false) }
        }

    override suspend fun getRelease(version: String): Result<FridaRelease> =
        safeApiCall { api.getRelease(version).toDomain(isLatest = false) }

}