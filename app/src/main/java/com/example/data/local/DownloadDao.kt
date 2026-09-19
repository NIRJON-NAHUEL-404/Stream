package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadedMediaEntity>>

    @Query("SELECT * FROM downloads WHERE videoId = :videoId LIMIT 1")
    fun getDownloadByVideoId(videoId: String): Flow<DownloadedMediaEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(entity: DownloadedMediaEntity): Long

    @Update
    suspend fun updateDownload(entity: DownloadedMediaEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM downloads WHERE videoId = :videoId")
    suspend fun deleteByVideoId(videoId: String)
}
