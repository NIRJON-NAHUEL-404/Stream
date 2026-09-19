package com.example.data.model

data class Comment(
    val id: String,
    val author: String,
    val text: String,
    val timeAgo: String,
    val likes: Int,
    val isLiked: Boolean = false
)
