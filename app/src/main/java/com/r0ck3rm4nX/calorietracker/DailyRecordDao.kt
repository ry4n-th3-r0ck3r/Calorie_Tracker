package com.r0ck3rm4nX.calorietracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(record: DailyRecord)

    @Query("SELECT * FROM DailyRecord WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyRecord?

    @Query("SELECT * FROM DailyRecord ORDER BY date DESC")
    suspend fun getAll(): List<DailyRecord>
}