package com.example.netflixtv.di

import android.content.Context
import com.example.netflixtv.data.StreamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideStreamRepository(
        @ApplicationContext context: Context
    ): StreamRepository {
        return StreamRepository()
    }
}
