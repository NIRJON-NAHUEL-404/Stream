package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DownloadDialog
import com.example.ui.components.PlaybackSpeedDialog
import com.example.ui.components.ResolutionDialog
import com.example.ui.screens.DataSaverScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen

@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val videos by viewModel.videosList.collectAsStateWithLifecycle()
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val isDownloadDialogVisible by viewModel.isDownloadDialogVisible.collectAsStateWithLifecycle()
    val isQualityDialogVisible by viewModel.isQualityDialogVisible.collectAsStateWithLifecycle()
    val isSpeedDialogVisible by viewModel.isSpeedDialogVisible.collectAsStateWithLifecycle()
    val isUltraDataSaverActive by viewModel.isUltraDataSaverActive.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Only show bottom bar when not in fullscreen mode
            if (!playerState.isFullscreen) {
                NavigationBar(modifier = Modifier.testTag("main_bottom_nav")) {
                    NavigationBarItem(
                        selected = selectedTab == MainTab.HOME,
                        onClick = { viewModel.selectTab(MainTab.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("হোম", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainTab.DOWNLOADS,
                        onClick = { viewModel.selectTab(MainTab.DOWNLOADS) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainTab.DOWNLOADS) Icons.Filled.DownloadDone else Icons.Outlined.DownloadDone,
                                contentDescription = "Downloads"
                            )
                        },
                        label = {
                            Text(
                                text = if (downloads.isNotEmpty()) "ডাউনলোড (${downloads.size})" else "ডাউনলোড",
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.testTag("nav_item_downloads")
                    )

                    NavigationBarItem(
                        selected = selectedTab == MainTab.DATA_SAVER,
                        onClick = { viewModel.selectTab(MainTab.DATA_SAVER) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == MainTab.DATA_SAVER) Icons.Filled.Savings else Icons.Outlined.Savings,
                                contentDescription = "Data Saver"
                            )
                        },
                        label = { Text("এমবি সেভার", fontSize = 11.sp) },
                        modifier = Modifier.testTag("nav_item_data_saver")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    MainTab.HOME -> {
                        HomeScreen(
                            playerController = viewModel.playerController,
                            playerState = playerState,
                            videos = videos,
                            selectedCategory = selectedCategory,
                            searchQuery = searchQuery,
                            isSearchActive = isSearchActive,
                            comments = comments,
                            isUltraDataSaverActive = isUltraDataSaverActive,
                            onCategorySelected = viewModel::setCategory,
                            onSearchQueryChange = viewModel::setSearchQuery,
                            onSearchActiveChange = viewModel::setSearchActive,
                            onVideoSelected = viewModel::selectVideo,
                            onQuickDownload = { video ->
                                viewModel.selectVideo(video)
                                viewModel.showDownloadDialog(true)
                            },
                            onLikeClick = viewModel::toggleLike,
                            onDislikeClick = viewModel::toggleDislike,
                            onSubscribeClick = viewModel::toggleSubscribe,
                            onDownloadClick = { viewModel.showDownloadDialog(true) },
                            onOpenQualityDialog = { viewModel.showQualityDialog(true) },
                            onOpenSpeedDialog = { viewModel.showSpeedDialog(true) },
                            onAddComment = viewModel::addComment,
                            onToggleDataSaver = viewModel::toggleUltraDataSaver
                        )
                    }

                    MainTab.DOWNLOADS -> {
                        DownloadsScreen(
                            downloads = downloads,
                            onPlayOffline = { download ->
                                viewModel.playOfflineDownload(download)
                                viewModel.selectTab(MainTab.HOME)
                            },
                            onDeleteDownload = viewModel::deleteDownload
                        )
                    }

                    MainTab.DATA_SAVER -> {
                        DataSaverScreen(
                            playerState = playerState,
                            isUltraDataSaverActive = isUltraDataSaverActive,
                            onToggleUltraDataSaver = viewModel::toggleUltraDataSaver
                        )
                    }

                    else -> {}
                }
            }
        }

        // Dialogs
        if (isQualityDialogVisible) {
            ResolutionDialog(
                currentQuality = playerState.currentQuality,
                onSelectQuality = { quality ->
                    viewModel.playerController.setQuality(quality)
                },
                onDismiss = { viewModel.showQualityDialog(false) }
            )
        }

        if (isDownloadDialogVisible) {
            DownloadDialog(
                videoTitle = playerState.currentVideo?.title ?: "ভিডিও",
                onConfirmDownload = { quality ->
                    viewModel.startDownload(quality)
                },
                onDismiss = { viewModel.showDownloadDialog(false) }
            )
        }

        if (isSpeedDialogVisible) {
            PlaybackSpeedDialog(
                currentSpeed = playerState.playbackSpeed,
                onSelectSpeed = { speed ->
                    viewModel.playerController.setPlaybackSpeed(speed)
                },
                onDismiss = { viewModel.showSpeedDialog(false) }
            )
        }
    }
}
