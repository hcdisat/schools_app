package com.hcdisat.schools.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
class CoroutinesDispatchersModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IODispatcher

    @IODispatcher
    @Provides
    fun bindsIODispatcher(): CoroutineDispatcher =
        Dispatchers.IO

    @Provides
    fun providesCoroutineScope(): CoroutineScope =
        CoroutineScope(Dispatchers.Main)
}