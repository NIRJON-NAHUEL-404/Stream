package com.example.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.example.player.PlayerUiState
import com.example.player.StreamingPlayerController
import java.util.Locale

@Composable
fun VideoPlayerView(
    playerController: StreamingPlayerController,
    uiState: PlayerUiState,
    onOpenQualityDialog: () -> Unit,
    onOpenSpeedDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val video = uiState.currentVideo

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(if (uiState.isFullscreen) 16f / 9f else 16f / 9f)
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                playerController.toggleControlsVisibility()
            }
            .testTag("video_player_surface")
    ) {
        if (uiState.isAudioOnly) {
            // PURE AUDIO STREAMING MODE UI: Low bandwidth mode
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF2C1016), Color(0xFF0F0B0D), Color.Black)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Headphones,
                                contentDescription = "Audio Mode",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "অডিও-অনলি স্ট্রিমিং মোড সক্রিয়",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "ভিডিও ডিকোডিং বন্ধ • ৯১% কম এমবি খরচ হচ্ছে",
                        fontSize = 11.sp,
                        color = Color(0xFFFF99AA)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AudioWaveVisualizer(isPlaying = uiState.isPlaying)
                }
            }
        } else {
            // NORMAL VIDEO VIEW (using TextureView to avoid emulator surface/EGL resource query errors)
            AndroidView(
                factory = { ctx ->
                    val playerView = LayoutInflater.from(ctx).inflate(
                        R.layout.layout_player_view,
                        FrameLayout(ctx),
                        false
                    ) as PlayerView
                    playerView.apply {
                        useController = false
                        player = playerController.exoPlayer
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                update = { playerView ->
                    if (playerView.player != playerController.exoPlayer) {
                        playerView.player = playerController.exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Buffering Indicator
        if (uiState.isBuffering && !uiState.hasError) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Error State Overlay
        if (uiState.hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = "Retry",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage ?: "ভিডিও লোড হতে সমস্যা হয়েছে",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { playerController.retryPlayback() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("retry_playback_button")
                    ) {
                        Text("পুনরায় চেষ্টা করুন", fontSize = 12.sp)
                    }
                }
            }
        }

        // PLAYER CONTROLS OVERLAY
        AnimatedVisibility(
            visible = uiState.isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = video?.title ?: "LiteTube Player",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 6.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Real-time MB tracker pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x99000000),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = String.format(Locale.US, "⚡ %.1f MB", uiState.dataUsedMb),
                                color = Color(0xFF4CAF50),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Audio Only toggle button
                        IconButton(
                            onClick = { playerController.toggleAudioOnly() },
                            modifier = Modifier.size(32.dp).testTag("toggle_audio_mode_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isAudioOnly) Icons.Default.Videocam else Icons.Default.Headphones,
                                contentDescription = "Toggle Audio Mode",
                                tint = if (uiState.isAudioOnly) MaterialTheme.colorScheme.primary else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Speed button
                        IconButton(
                            onClick = onOpenSpeedDialog,
                            modifier = Modifier.size(32.dp).testTag("speed_button")
                        ) {
                            Text(
                                text = "${uiState.playbackSpeed}x",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Quality Badge button
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onOpenQualityDialog() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("quality_button")
                        ) {
                            Text(
                                text = uiState.currentQuality.badge,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                // CENTER CONTROLS
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = { playerController.rewind10Seconds() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .testTag("rewind_10_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay10,
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Play/Pause
                    IconButton(
                        onClick = { playerController.togglePlayPause() },
                        modifier = Modifier
                            .size(56.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = { playerController.forward10Seconds() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                            .testTag("forward_10_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Forward10,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                // BOTTOM CONTROLS
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatTime(uiState.currentPositionMs)} / ${formatTime(uiState.durationMs)}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        IconButton(
                            onClick = { playerController.toggleFullscreen() },
                            modifier = Modifier.size(30.dp).testTag("fullscreen_button")
                        ) {
                            Icon(
                                imageVector = if (uiState.isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Seek Slider
                    val maxDuration = uiState.durationMs.coerceAtLeast(1L).toFloat()
                    val currentPos = uiState.currentPositionMs.toFloat().coerceIn(0f, maxDuration)

                    Slider(
                        value = currentPos,
                        onValueChange = { newPos ->
                            playerController.seekTo(newPos.toLong())
                        },
                        valueRange = 0f..maxDuration,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .testTag("video_seek_slider")
                    )
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}
