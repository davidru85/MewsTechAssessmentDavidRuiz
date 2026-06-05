package com.mews.guestroom.feature.controls.data.di

import com.mews.guestroom.feature.controls.data.source.ControlsDataSource
import com.mews.guestroom.feature.controls.data.source.LiveControlsDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the `live`-flavor [ControlsDataSource]. Compiled only into the `live`
 * variant, so it is the sole binding in that Hilt graph — no clash with the
 * `mock` flavor's module.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ControlsLiveModule {

    @Binds
    @Singleton
    abstract fun bindControlsDataSource(impl: LiveControlsDataSource): ControlsDataSource
}
