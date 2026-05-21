package com.fryda.app.data.remote.api

import com.fryda.app.core.constants.Constants
import com.fryda.app.data.remote.dto.GithubRelease
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET(Constants.RELEASES_ENDPOINT)
    suspend fun listReleases(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30,
    ): List<GithubRelease>

    @GET(Constants.RELEASE_BY_TAG_ENDPOINT)
    suspend fun getRelease(
        @Path("tag") tag: String,
    ): GithubRelease

    @GET(Constants.LATEST_RELEASE_ENDPOINT)
    suspend fun getLatestRelease(): GithubRelease
}