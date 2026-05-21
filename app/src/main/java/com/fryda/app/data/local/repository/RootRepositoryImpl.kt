package com.fryda.app.data.local.repository

import com.fryda.app.core.root.RootChecker
import com.fryda.app.domain.model.RootStatus
import com.fryda.app.domain.repository.RootRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootRepositoryImpl @Inject constructor(
    private val rootChecker: RootChecker
) : RootRepository {
    override suspend fun checkRoot(): RootStatus = rootChecker.checkRoot()
    override suspend fun isRooted(): Boolean = rootChecker.isRooted()
}