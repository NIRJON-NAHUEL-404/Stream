package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_usage")
data class DataUsageEntity(
    @PrimaryKey val id: Int = 1,
    val totalMbStreamed: Double = 1.4,
    val totalMbSaved: Double = 14.8,
    val videosWatched: Int = 3,
    val isUltraDataSaverEnabled: Boolean = true
)
