package com.otprelay.app.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [PendingOtpEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class OtpRelayDatabase : RoomDatabase() {
    abstract fun pendingOtpDao(): PendingOtpDao
}
