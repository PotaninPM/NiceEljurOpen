package com.team.feature_messages.domain

import com.team.feature_messages.data.model.MessageFolder
import com.team.feature_messages.data.model.MessagesResponseInfoAll

interface MessagesRepository {
    suspend fun getMessages(
        authToken: String,
        folder: MessageFolder,
        page: Int,
        searchQuery: String? = null,
        unreadOnly: Boolean = false
    ): MessagesResponseInfoAll
}