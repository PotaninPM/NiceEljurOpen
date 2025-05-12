package com.team.feature_messages.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    val id: String?,
    val subject: String,
    @SerializedName("short_text") val shortText: String?,
    val date: String,
    val unread: Boolean,
    @SerializedName("with_files") val withFiles: Boolean,
    @SerializedName("with_resources") val withResources: Boolean,
    @SerializedName("user_from") val userFrom: UserFrom?,
    @SerializedName("users_to") val usersTo: List<UserFrom>?
)

data class UserFrom(
    val name: String,
    @SerializedName("lastname") val lastName: String?,
    @SerializedName("firstname") val firstName: String?,
    @SerializedName("middlename") val middleName: String?
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