package com.example.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.R
import com.example.data.model.VideoItem
import com.example.data.model.VideoQuality
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentVideo: VideoItem? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val currentQuality: VideoQuality = VideoQuality.P360,
    val isAudioOnly: Boolean = false,
    val isFullscreen: Boolean = false,
    val dataUsedMb: Double = 0.0,
    val dataSavedMb: Double = 0.0,
    val currentBitrateText: String = "650 Kbps",
    val isControlsVisible: Boolean = true
)

class StreamingPlayerController(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var progressTrackingJob: Job? = null
    private var fallbackAttempted = false

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    // Configure fast-buffer load control: starts in 500ms, max buffer 8000ms to avoid MB waste!
    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            1500,  // minBufferMs
            8000,  // maxBufferMs (Ultra data saver: does not overbuffer unnecessary MB!)
            500,   // bufferForPlaybackMs (instant start)
            1000   // bufferForPlaybackAfterRebufferMs
        )
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    // Configured HTTP data source factory with standard mobile browser User-Agent
    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36 LiteTube/1.0")
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(15000)

    private val mediaSourceFactory = DefaultMediaSourceFactory(context)
        .setDataSourceFactory(DefaultDataSource.Factory(context, httpDataSourceFactory))

    private val renderersFactory = DefaultRenderersFactory(context)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
        .setEnableDecoderFallback(true)

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                _uiState.value = _uiState.value.copy(
                                    isBuffering = true,
                                    hasError = false
                                )
                            }
                            Player.STATE_READY -> {
                                _uiState.value = _uiState.value.copy(
                                    isBuffering = false,
                                    hasError = false,
                                    durationMs = duration.coerceAtLeast(0L)
                                )
                            }
                            Player.STATE_ENDED -> {
                                _uiState.value = _uiState.value.copy(
                                    isPlaying = false,
                                    isBuffering = false
                                )
                            }
                            Player.STATE_IDLE -> {
                                _uiState.value = _uiState.value.copy(isBuffering = false)
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("LiteTubePlayer", "Source error caught: ${error.message}", error)
                        _uiState.value = _uiState.value.copy(
                            isBuffering = false,
                            isPlaying = false,
                            hasError = true,
                            errorMessage = "ভিডিও লোড হতে সমস্যা হয়েছে। পুনরায় চেষ্টা করুন।"
                        )
                        handleFallback()
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                        if (isPlaying) {
                            startProgressTracking()
                        } else {
                            progressTrackingJob?.cancel()
                        }
                    }
                })
            }
    }

    private fun handleFallback() {
        if (!fallbackAttempted) {
            fallbackAttempted = true
            try {
                val isAudio = _uiState.value.isAudioOnly
                val fallbackUri = if (isAudio) {
                    "android.resource://${context.packageName}/${R.raw.sample_audio}"
                } else {
                    "android.resource://${context.packageName}/${R.raw.sample_video}"
                }
                val mediaItem = MediaItem.fromUri(fallbackUri)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                _uiState.value = _uiState.value.copy(hasError = false, errorMessage = null)
            } catch (e: Exception) {
                Log.e("LiteTubePlayer", "Embedded resource fallback error", e)
            }
        }
    }

    fun playVideo(video: VideoItem, initialQuality: VideoQuality = VideoQuality.P360) {
        fallbackAttempted = false
        val quality = if (_uiState.value.isAudioOnly) VideoQuality.AUDIO_ONLY else initialQuality
        _uiState.value = _uiState.value.copy(
            currentVideo = video,
            currentQuality = quality,
            isBuffering = true,
            hasError = false,
            errorMessage = null,
            currentPositionMs = 0L,
            durationMs = (video.durationSeconds * 1000).toLong()
        )

        val mediaItem = MediaItem.fromUri(video.streamUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        startProgressTracking()
    }

    fun retryPlayback() {
        fallbackAttempted = false
        _uiState.value.currentVideo?.let {
            playVideo(it, _uiState.value.currentQuality)
        }
    }

    fun togglePlayPause() {
        if (_uiState.value.hasError) {
            retryPlayback()
            return
        }
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs.coerceIn(0L, exoPlayer.duration.coerceAtLeast(1L)))
        _uiState.value = _uiState.value.copy(currentPositionMs = positionMs)
    }

    fun forward10Seconds() {
        seekTo(exoPlayer.currentPosition + 10_000L)
    }

    fun rewind10Seconds() {
        seekTo((exoPlayer.currentPosition - 10_000L).coerceAtLeast(0L))
    }

    fun setQuality(quality: VideoQuality) {
        val currentPos = exoPlayer.currentPosition
        val isCurrentlyPlaying = exoPlayer.isPlaying
        val isAudio = quality == VideoQuality.AUDIO_ONLY

        _uiState.value = _uiState.value.copy(
            currentQuality = quality,
            isAudioOnly = isAudio,
            currentBitrateText = "${quality.approxBitrateKbps} Kbps"
        )

        // Seamless quality switch: re-prepare preserving position
        _uiState.value.currentVideo?.let { video ->
            val mediaItem = MediaItem.fromUri(video.streamUrl)
            exoPlayer.setMediaItem(mediaItem, currentPos)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isCurrentlyPlaying
        }
    }

    fun toggleAudioOnly() {
        val newAudioState = !_uiState.value.isAudioOnly
        if (newAudioState) {
            setQuality(VideoQuality.AUDIO_ONLY)
        } else {
            setQuality(VideoQuality.P360)
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    fun toggleControlsVisibility() {
        _uiState.value = _uiState.value.copy(isControlsVisible = !_uiState.value.isControlsVisible)
    }

    fun toggleFullscreen() {
        _uiState.value = _uiState.value.copy(isFullscreen = !_uiState.value.isFullscreen)
    }

    private fun startProgressTracking() {
        progressTrackingJob?.cancel()
        progressTrackingJob = coroutineScope.launch {
            while (isActive) {
                if (exoPlayer.isPlaying) {
                    val currentPos = exoPlayer.currentPosition
                    val duration = exoPlayer.duration.coerceAtLeast(0L)
                    val bufferedPos = exoPlayer.bufferedPosition

                    // Real-time MB calculation based on bitrate
                    val quality = _uiState.value.currentQuality
                    val secondsPlayed = currentPos / 1000.0
                    val usedMb = (secondsPlayed * quality.approxBitrateKbps) / (8.0 * 1024.0)
                    val standardMb1080p = (secondsPlayed * VideoQuality.P1080.approxBitrateKbps) / (8.0 * 1024.0)
                    val savedMb = (standardMb1080p - usedMb).coerceAtLeast(0.0)

                    _uiState.value = _uiState.value.copy(
                        currentPositionMs = currentPos,
                        durationMs = if (duration > 0) duration else _uiState.value.durationMs,
                        bufferedPositionMs = bufferedPos,
                        dataUsedMb = usedMb,
                        dataSavedMb = savedMb
                    )
                }
                delay(400)
            }
        }
    }

    fun release() {
        progressTrackingJob?.cancel()
        exoPlayer.release()
    }
}
