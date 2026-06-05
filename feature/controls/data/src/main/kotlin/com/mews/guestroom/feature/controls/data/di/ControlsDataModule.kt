package com.mews.guestroom.feature.controls.data.di

import com.mews.guestroom.feature.controls.data.repository.ControlsRepositoryImpl
import com.mews.guestroom.feature.controls.data.source.ControlsDataSource
import com.mews.guestroom.feature.controls.data.source.MockControlsDataSource
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
 * Wires the Controls feature's data layer. The mock data source is bound today;
 * swapping to a live source means changing only the [ControlsDataSource] binding
 * here (or in a `live` flavor source set) — domain and presentation are untouched.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ControlsDataModule {

    @Binds
    @Singleton
    abstract fun bindControlsRepository(impl: ControlsRepositoryImpl): ControlsRepository

    @Binds
    @Singleton
    abstract fun bindControlsDataSource(impl: MockControlsDataSource): ControlsDataSource

    companion object {
        @Provides
        @Singleton
        @ControlsScope
        fun provideControlsScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
