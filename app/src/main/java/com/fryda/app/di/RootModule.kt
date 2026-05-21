package com.fryda.app.di

import com.fryda.app.data.local.repository.RootRepositoryImpl
import com.fryda.app.domain.repository.RootRepository
import com.fryda.app.domain.use_cases.root_use_cases.CheckRootUseCase
import com.fryda.app.domain.use_cases.root_use_cases.IsRootedUseCase
import com.fryda.app.domain.use_cases.root_use_cases.RootUseCases
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RootRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRootRepository(
        rootRepositoryImpl: RootRepositoryImpl
    ): RootRepository
}

@Module
@InstallIn(SingletonComponent::class)
object RootUseCaseModule {

    @Provides
    @Singleton
    fun provideRootUseCases(
        checkRootUseCase: CheckRootUseCase,
        isRootedUseCase: IsRootedUseCase
    ): RootUseCases {
        return RootUseCases(
            checkRoot = checkRootUseCase,
            isRooted = isRootedUseCase
        )
    }

    @Provides
    @Singleton
    fun provideCheckRootUseCase(
        repository: RootRepository
    ): CheckRootUseCase = CheckRootUseCase(repository)

    @Provides
    @Singleton
    fun provideIsRootedUseCase(
        repository: RootRepository
    ): IsRootedUseCase = IsRootedUseCase(repository)
}