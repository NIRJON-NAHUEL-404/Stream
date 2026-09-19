package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadedMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val title: String,
    val channelName: String,
    val durationText: String,
    val qualityLabel: String,
    val sizeMb: Double,
    val isAudioOnly: Boolean,
    val thumbnailRes: Int?,
    val streamUrl: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = true,
    val progress: Float = 1.0f
)
