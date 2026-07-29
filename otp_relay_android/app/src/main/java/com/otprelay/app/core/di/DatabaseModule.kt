package com.otprelay.app.core.di

import android.content.Context
import androidx.room.Room
import com.otprelay.app.core.storage.OtpRelayDatabase
import com.otprelay.app.core.storage.PendingOtpDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideOtpRelayDatabase(
        @ApplicationContext context: Context
    ): OtpRelayDatabase {
        return Room.databaseBuilder(
            context,
            OtpRelayDatabase::class.java,
            "otp_relay.db"
        ).build()
    }

    @Provides
    fun providePendingOtpDao(database: OtpRelayDatabase): PendingOtpDao {
        return database.pendingOtpDao()
    }
}
