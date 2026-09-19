package com.example.data.model

data class VideoItem(
    val id: String,
    val title: String,
    val channelName: String,
    val channelAvatar: String,
    val subscribers: String,
    val views: String,
    val uploadDate: String,
    val durationText: String,
    val durationSeconds: Int,
    val description: String,
    val category: String,
    val streamUrl: String,
    val localThumbnailRes: Int? = null,
    val remoteThumbnailUrl: String = "",
    val likesCount: Int,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSubscribed: Boolean = false,
    val tags: List<String> = emptyList(),
    val commentsCount: Int = 24
)
