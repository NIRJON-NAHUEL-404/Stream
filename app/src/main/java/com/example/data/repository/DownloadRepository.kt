package com.example.data.repository

import com.example.data.local.DownloadDao
import com.example.data.local.DownloadedMediaEntity
import com.example.data.model.VideoItem
import com.example.data.model.VideoQuality
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class DownloadRepository(private val downloadDao: DownloadDao) {

    val allDownloads: Flow<List<DownloadedMediaEntity>> = downloadDao.getAllDownloads()

    fun getDownloadForVideo(videoId: String): Flow<DownloadedMediaEntity?> {
        return downloadDao.getDownloadByVideoId(videoId)
    }

    suspend fun startDownload(
        video: VideoItem,
        quality: VideoQuality,
        onProgress: suspend (Float) -> Unit = {}
    ) {
        val sizeMb = when (quality) {
            VideoQuality.AUDIO_ONLY -> 1.4
            VideoQuality.P144 -> 2.8
            VideoQuality.P240 -> 5.2
            VideoQuality.P360 -> 9.6
            VideoQuality.P480 -> 18.2
            VideoQuality.P720 -> 34.0
            VideoQuality.P1080 -> 62.0
            VideoQuality.AUTO -> 8.5
        }

        val initialEntity = DownloadedMediaEntity(
            videoId = video.id,
            title = video.title,
            channelName = video.channelName,
            durationText = video.durationText,
            qualityLabel = quality.label,
            sizeMb = sizeMb,
            isAudioOnly = quality == VideoQuality.AUDIO_ONLY,
            thumbnailRes = video.localThumbnailRes,
            streamUrl = video.streamUrl,
            isCompleted = false,
            progress = 0.1f
        )

        val id = downloadDao.insertDownload(initialEntity).toInt()

        // Simulate fast progressive download with real progress updates
        for (i in 2..10) {
            delay(180)
            val currentProgress = i / 10f
            downloadDao.updateDownload(
                initialEntity.copy(
                    id = id,
                    progress = currentProgress,
                    isCompleted = i == 10
                )
            )
            onProgress(currentProgress)
        }
    }

    suspend fun removeDownload(id: Int) {
        downloadDao.deleteById(id)
    }

    suspend fun removeDownloadByVideoId(videoId: String) {
        downloadDao.deleteByVideoId(videoId)
    }
}
