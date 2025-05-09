package com.team.feature_messages.data.model

data class Message(
    val id: String?,
    val subject: String,
    val shortText: String?,
    val date: String,
    val unread: Boolean,
    val withFiles: Boolean,
    val withResources: Boolean,
    val userFrom: UserFrom?
)

data class UserFrom(
    val name: String,
    val lastName: String?,
    val firstName: String?,
    val middleName: String?
)

data class MessagesResponseInfoAll(
    val response: MessageResponseInfo,
)

data class MessageResponseInfo(
    val state: Int,
    val error: String?,
    val result: MessagesResponse?
)

data class MessagesResponse(
    val total: String?,
    val count: Int,
    val messages: List<Message>
)

enum class MessageFolder {
    INBOX, SENT
} 