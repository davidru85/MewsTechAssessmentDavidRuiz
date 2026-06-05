package com.mews.guestroom.feature.controls.data.di

import com.mews.guestroom.feature.controls.data.repository.ControlsRepositoryImpl
import com.mews.guestroom.feature.controls.domain.repository.ControlsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/** Qualifies the application-lifetime scope that drives the mock room simulation. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ControlsScope

/**
 * Wires the Controls feature's flavor-agnostic data layer: the repository binding
 * and the simulation scope. The concrete [com.mews.guestroom.feature.controls.data.source.ControlsDataSource]
 * binding lives in the per-flavor modules (`ControlsMockModule` / `ControlsLiveModule`),
 * which is exactly where the prototype-to-production seam sits — swapping the flavor
 * swaps the data source with no change to domain, presentation, or `:app`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ControlsDataModule {

    @Binds
    @Singleton
    abstract fun bindControlsRepository(impl: ControlsRepositoryImpl): ControlsRepository

    companion object {
        @Provides
        @Singleton
        @ControlsScope
        fun provideControlsScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
