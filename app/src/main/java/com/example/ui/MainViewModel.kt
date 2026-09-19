package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.VideoDataSource
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedMediaEntity
import com.example.data.model.Comment
import com.example.data.model.VideoItem
import com.example.data.model.VideoQuality
import com.example.data.repository.DataUsageRepository
import com.example.data.repository.DownloadRepository
import com.example.player.PlayerUiState
import com.example.player.StreamingPlayerController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    TRENDING,
    DOWNLOADS,
    DATA_SAVER
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val downloadRepo = DownloadRepository(database.downloadDao())
    private val dataUsageRepo = DataUsageRepository(database.dataUsageDao())

    val playerController = StreamingPlayerController(application)
    val playerState: StateFlow<PlayerUiState> = playerController.uiState

    val downloads: StateFlow<List<DownloadedMediaEntity>> = downloadRepo.allDownloads
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow("সকল")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    private val _videosList = MutableStateFlow(VideoDataSource.sampleVideos)
    val videosList: StateFlow<List<VideoItem>> = _videosList.asStateFlow()

    private val _comments = MutableStateFlow(VideoDataSource.getSampleComments("vid_1"))
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    private val _isDownloadDialogVisible = MutableStateFlow(false)
    val isDownloadDialogVisible: StateFlow<Boolean> = _isDownloadDialogVisible.asStateFlow()

    private val _isQualityDialogVisible = MutableStateFlow(false)
    val isQualityDialogVisible: StateFlow<Boolean> = _isQualityDialogVisible.asStateFlow()

    private val _isSpeedDialogVisible = MutableStateFlow(false)
    val isSpeedDialogVisible: StateFlow<Boolean> = _isSpeedDialogVisible.asStateFlow()

    private val _isUltraDataSaverActive = MutableStateFlow(true)
    val isUltraDataSaverActive: StateFlow<Boolean> = _isUltraDataSaverActive.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        // Automatically start with the first featured video paused or prepared with ultra-fast buffer
        playerController.playVideo(VideoDataSource.sampleVideos.first(), VideoQuality.P360)
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) _searchQuery.value = ""
    }

    fun selectVideo(video: VideoItem) {
        val initialQuality = if (_isUltraDataSaverActive.value) VideoQuality.P240 else VideoQuality.P360
        playerController.playVideo(video, initialQuality)
        _comments.value = VideoDataSource.getSampleComments(video.id)
    }

    fun playOfflineDownload(download: DownloadedMediaEntity) {
        val simulatedVideo = VideoItem(
            id = download.videoId,
            title = download.title,
            channelName = download.channelName,
            channelAvatar = "",
            subscribers = "অফলাইন",
            views = "অফলাইন প্লেব্যাক",
            uploadDate = "ডাউনলোড করা ফাইল",
            durationText = download.durationText,
            durationSeconds = 600,
            description = "অফলাইন ডাউনলোড ফাইল। ইন্টারনেট ছাড়াই খুব দ্রুত লোডিং হবে এবং কোন এমবি খরচ হবে না।",
            category = "অফলাইন",
            streamUrl = download.streamUrl,
            localThumbnailRes = download.thumbnailRes,
            likesCount = 100
        )
        playerController.playVideo(
            simulatedVideo,
            if (download.isAudioOnly) VideoQuality.AUDIO_ONLY else VideoQuality.P360
        )
        _snackbarMessage.value = "অফলাইন প্লে শুরু হয়েছে (০ এমবি খরচ)"
    }

    fun toggleLike(video: VideoItem) {
        _videosList.value = _videosList.value.map {
            if (it.id == video.id) {
                val newLiked = !it.isLiked
                it.copy(
                    isLiked = newLiked,
                    likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1,
                    isDisliked = false
                )
            } else it
        }
    }

    fun toggleDislike(video: VideoItem) {
        _videosList.value = _videosList.value.map {
            if (it.id == video.id) {
                it.copy(
                    isDisliked = !it.isDisliked,
                    isLiked = false
                )
            } else it
        }
    }

    fun toggleSubscribe(video: VideoItem) {
        _videosList.value = _videosList.value.map {
            if (it.channelName == video.channelName) {
                it.copy(isSubscribed = !it.isSubscribed)
            } else it
        }
        val isSub = _videosList.value.find { it.channelName == video.channelName }?.isSubscribed == true
        _snackbarMessage.value = if (isSub) "চ্যানেল সাবস্ক্রাইব করা হয়েছে!" else "আনসাবস্ক্রাইব করা হয়েছে"
    }

    fun addComment(text: String) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            author = "আপনি (User)",
            text = text.trim(),
            timeAgo = "এখনই",
            likes = 0
        )
        _comments.value = listOf(newComment) + _comments.value
        _snackbarMessage.value = "মন্তব্য পোস্ট করা হয়েছে"
    }

    fun showDownloadDialog(show: Boolean) {
        _isDownloadDialogVisible.value = show
    }

    fun showQualityDialog(show: Boolean) {
        _isQualityDialogVisible.value = show
    }

    fun showSpeedDialog(show: Boolean) {
        _isSpeedDialogVisible.value = show
    }

    fun startDownload(quality: VideoQuality) {
        val video = playerState.value.currentVideo ?: return
        showDownloadDialog(false)
        _snackbarMessage.value = "ডাউনলোড শুরু হয়েছে (${quality.label})..."
        viewModelScope.launch {
            downloadRepo.startDownload(video, quality)
            _snackbarMessage.value = "ডাউনলোড সম্পন্ন হয়েছে! অফলাইনে দেখুন।"
        }
    }

    fun deleteDownload(id: Int) {
        viewModelScope.launch {
            downloadRepo.removeDownload(id)
            _snackbarMessage.value = "ডাউনলোড মুছে ফেলা হয়েছে"
        }
    }

    fun toggleUltraDataSaver() {
        val newValue = !_isUltraDataSaverActive.value
        _isUltraDataSaverActive.value = newValue
        if (newValue) {
            playerController.setQuality(VideoQuality.P240)
            _snackbarMessage.value = "চরম ডেটা সাভার চালু: ২৪০পি অ্যাক্টিভ (সীমিত এমবি খরচ)"
        } else {
            playerController.setQuality(VideoQuality.P480)
            _snackbarMessage.value = "স্ট্যান্ডার্ড মোড সক্রিয়"
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}
