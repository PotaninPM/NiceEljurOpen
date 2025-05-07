package com.team.feature_messages.data.repository

import com.team.feature_messages.data.remote.MessagesApi
import com.team.feature_messages.domain.MessagesRepository

class MessagesRepositoryImpl(
    private val api: MessagesApi
): MessagesRepository {

}