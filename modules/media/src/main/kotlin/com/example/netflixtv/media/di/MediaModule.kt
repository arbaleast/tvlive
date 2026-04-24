package com.example.netflixtv.media.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.example.netflixtv.media.PlayerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @UnstableApi
    @Provides
    @Singleton
    fun providePlayerManager(
        @ApplicationContext context: Context
    ): PlayerManager {
        return PlayerManager(context)
    }
}