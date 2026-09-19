package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.VideoDataSource
import com.example.data.model.Comment
import com.example.data.model.VideoItem
import com.example.data.model.VideoQuality
import com.example.player.PlayerUiState
import com.example.player.StreamingPlayerController
import com.example.ui.components.PlayerDetailSection
import com.example.ui.components.VideoCard
import com.example.ui.components.VideoPlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerController: StreamingPlayerController,
    playerState: PlayerUiState,
    videos: List<VideoItem>,
    selectedCategory: String,
    searchQuery: String,
    isSearchActive: Boolean,
    comments: List<Comment>,
    isUltraDataSaverActive: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onVideoSelected: (VideoItem) -> Unit,
    onQuickDownload: (VideoItem) -> Unit,
    onLikeClick: (VideoItem) -> Unit,
    onDislikeClick: (VideoItem) -> Unit,
    onSubscribeClick: (VideoItem) -> Unit,
    onDownloadClick: () -> Unit,
    onOpenQualityDialog: () -> Unit,
    onOpenSpeedDialog: () -> Unit,
    onAddComment: (String) -> Unit,
    onToggleDataSaver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeVideo = playerState.currentVideo ?: videos.firstOrNull()

    // Filter videos by category and search
    val filteredVideos = videos.filter { video ->
        val matchesCategory = selectedCategory == "সকল" || video.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.channelName.contains(searchQuery, ignoreCase = true) ||
                video.tags.any { it.contains(searchQuery, ignoreCase = true) }
        matchesCategory && matchesSearch
    }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // TOP APP BAR
        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("ভিডিও বা চ্যানেল খুঁজুন...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f).testTag("search_text_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )
                IconButton(
                    onClick = { onSearchActiveChange(false) },
                    modifier = Modifier.testTag("close_search_button")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel Search")
                }
            }
        } else {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ElectricBolt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LiteTube",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            letterSpacing = (-0.5).sp
                        )
                    }
                },
                actions = {
                    // Ultra Data Saver status pill
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isUltraDataSaverActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = null,
                                tint = if (isUltraDataSaverActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isUltraDataSaverActive) "সীমিত এমবি" else "স্বাভাবিক",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUltraDataSaverActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = { onSearchActiveChange(true) },
                        modifier = Modifier.testTag("open_search_button")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

        // STICKY PLAYER AT THE TOP (YouTube style continuous playback)
        VideoPlayerView(
            playerController = playerController,
            uiState = playerState,
            onOpenQualityDialog = onOpenQualityDialog,
            onOpenSpeedDialog = onOpenSpeedDialog
        )

        // SCROLLABLE CONTENT: Active Video Details + Category Pills + Up Next Videos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Active video details
            if (activeVideo != null) {
                item(key = "active_video_details") {
                    PlayerDetailSection(
                        video = activeVideo,
                        playerState = playerState,
                        comments = comments,
                        onLikeClick = { onLikeClick(activeVideo) },
                        onDislikeClick = { onDislikeClick(activeVideo) },
                        onSubscribeClick = { onSubscribeClick(activeVideo) },
                        onDownloadClick = onDownloadClick,
                        onAudioToggle = { playerController.toggleAudioOnly() },
                        onAddComment = onAddComment
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 1.dp
                    )
                }
            }

            // Categories Bar
            item(key = "categories_row") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VideoDataSource.categories.forEach { category ->
                        val isSelected = category == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }

            // Section Header: পরবর্তী ভিডিও (Up Next)
            item(key = "up_next_header") {
                Text(
                    text = if (searchQuery.isNotBlank()) "অনুসন্ধানের ফলাফল (${filteredVideos.size})" else "পরবর্তী ভিডিও ও সুপারিশ (Up Next)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Video items list
            items(
                items = filteredVideos.filter { it.id != activeVideo?.id },
                key = { it.id }
            ) { video ->
                VideoCard(
                    video = video,
                    onClick = { onVideoSelected(video) },
                    onQuickDownload = { onQuickDownload(video) }
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                    thickness = 0.8.dp
                )
            }
        }
    }
}
