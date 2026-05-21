package com.fryda.app.domain.repository

import com.fryda.app.domain.model.RootStatus

interface RootRepository {
    suspend fun checkRoot(): RootStatus
    suspend fun isRooted(): Boolean
}