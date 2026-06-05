package com.mews.guestroom.feature.controls.data.di

import com.mews.guestroom.feature.controls.data.source.ControlsDataSource
import com.mews.guestroom.feature.controls.data.source.MockControlsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the `mock`-flavor [ControlsDataSource] (the in-memory room simulation).
 * Compiled only into the `mock` variant, so it is the sole binding in that Hilt
 * graph — no clash with the `live` flavor's module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ControlsMockModule {

    @Binds
    @Singleton
    abstract fun bindControlsDataSource(impl: MockControlsDataSource): ControlsDataSource
}
