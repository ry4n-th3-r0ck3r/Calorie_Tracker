package com.r0ck3rm4nX.calorietracker

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DailyRecord::class],
    version = 1
)
abstract class CalorieDatabase : RoomDatabase() {

    abstract fun dailyRecordDao(): DailyRecordDao
}