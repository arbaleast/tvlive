package com.example.netflixtv.di

import com.example.netflixtv.data.StreamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStreamRepository(): StreamRepository {
        return StreamRepository()
    }
}
