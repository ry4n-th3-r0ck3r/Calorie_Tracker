package com.r0ck3rm4nX.calorietracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyRecord(
    @PrimaryKey val date: String,
    val calorieGoal: Int,
    val caloriesConsumed: Int,
    val caloriesBurned: Int,
    val weight: Double?,
    val height: Double?,
    val age: Int?,
    val sex: String?
)