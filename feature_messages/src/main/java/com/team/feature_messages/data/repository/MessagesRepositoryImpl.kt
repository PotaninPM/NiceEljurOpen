package com.team.feature_messages.data.repository

import com.team.feature_messages.data.model.MessageFolder
import com.team.feature_messages.data.model.MessagesResponseInfoAll
import com.team.feature_messages.data.remote.MessagesApi
import com.team.feature_messages.domain.MessagesRepository

class MessagesRepositoryImpl(
    private val api: MessagesApi
): MessagesRepository {
    override suspend fun getMessages(
        authToken: String,
        folder: MessageFolder,
        page: Int,
        searchQuery: String?,
        unreadOnly: Boolean
    ): MessagesResponseInfoAll {
        val ans = api.getMessages(
            authToken = authToken,
            folder = folder.name.lowercase(),
            unreadOnly = if (unreadOnly) "yes" else "no",
            page = page,
            filter = searchQuery
        )

        return ans
    }
}