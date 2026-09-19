package com.example.data.repository

import com.example.data.local.DataUsageDao
import com.example.data.local.DataUsageEntity
import kotlinx.coroutines.flow.Flow

class DataUsageRepository(private val dataUsageDao: DataUsageDao) {
    val dataUsage: Flow<DataUsageEntity?> = dataUsageDao.getDataUsage()

    suspend fun addUsage(streamedMb: Double, savedMb: Double) {
        // Will be updated dynamically during video streaming
        dataUsageDao.save(
            DataUsageEntity(
                id = 1,
                totalMbStreamed = streamedMb,
                totalMbSaved = savedMb,
                videosWatched = 4,
                isUltraDataSaverEnabled = true
            )
        )
    }

    suspend fun toggleDataSaver(enabled: Boolean, current: DataUsageEntity?) {
        val existing = current ?: DataUsageEntity()
        dataUsageDao.save(existing.copy(isUltraDataSaverEnabled = enabled))
    }
}
