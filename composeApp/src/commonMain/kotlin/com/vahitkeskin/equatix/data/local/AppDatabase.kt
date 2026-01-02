package com.vahitkeskin.equatix.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vahitkeskin.equatix.data.local.dao.GameScoreDao
import com.vahitkeskin.equatix.data.local.entity.GameScoreEntity
import com.vahitkeskin.equatix.data.local.converters.Converters // Enum çeviricileri için (Aşağıda yapacağız)

@Database(entities = [GameScoreEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameScoreDao(): GameScoreDao
}