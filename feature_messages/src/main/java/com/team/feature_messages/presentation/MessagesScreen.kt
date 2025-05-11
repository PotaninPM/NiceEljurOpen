package com.team.feature_messages.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.common.components.UserInfoTopBar
import com.team.common.components.icons.BellIcon
import com.team.common.components.icons.FilterIcon
import com.team.common.functions.formatDateTime
import com.team.feature_messages.R
import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
import com.team.feature_messages.presentation.viewmodel.MessagesViewModel
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState { MessageFolder.entries.size }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadInitialMessages()
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onFolderSelected(MessageFolder.entries[pagerState.currentPage])
    }

    Scaffold(
        modifier = Modifier
            .padding(top = 4.dp),
        topBar = {
            UserInfoTopBar(
                personName = uiState.personName,
                role = uiState.personRole,
                icons = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        BellIcon {

                        }

                        FilterIcon {

                        }
                    }
                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            MessagesTopBar(
                searchQuery = when (uiState.currentFolder) {
                    MessageFolder.INBOX -> uiState.inboxState.searchQuery
                    MessageFolder.SENT -> uiState.sentState.searchQuery
                },
                onSearchQueryChange = { query -> viewModel.onSearchQueryChanged(query) },
                unreadOnly = when (uiState.currentFolder) {
                    MessageFolder.INBOX -> uiState.inboxState.unreadOnly
                    MessageFolder.SENT -> uiState.sentState.unreadOnly
                },
                onUnreadOnlyChange = { unreadOnly -> viewModel.onUnreadOnlyChanged(unreadOnly) }
            )

            TabRow(
                selectedTabIndex = pagerState.currentPage
            ) {
                MessageFolder.entries.forEachIndexed { index, folder ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(folder.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                key = { MessageFolder.entries[it].name }
            ) { page ->
                val folder = MessageFolder.entries[page]
                val folderState = when (folder) {
                    MessageFolder.INBOX -> uiState.inboxState
                    MessageFolder.SENT -> uiState.sentState
                }
                
                Box(modifier = Modifier.fillMaxSize()) {
                    if (folderState.messages.isEmpty() && uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        MessagesContent(
                            messages = folderState.messages,
                            isLoading = uiState.isLoading,
                            hasMoreMessages = folderState.messages.size < folderState.totalMessages,
                            onLoadMore = { viewModel.loadNextPage() },
                            currentFolder = folder
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    unreadOnly: Boolean,
    onUnreadOnlyChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp)
    ) {
        CustomSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = "Поиск сообщений..."
        )
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Поиск...",
) {
    var focusState by remember { mutableStateOf(false) }
    var textFieldValue by remember(query) { mutableStateOf(query) }

    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    val imeVisible = imeBottomPx > 0

    LaunchedEffect(textFieldValue) {
        onQueryChange(textFieldValue)
    }

    TextField(
        value = textFieldValue,
        onValueChange = { newValue -> 
            textFieldValue = newValue
        },
        shape = MaterialTheme.shapes.large,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = Color.White
        ),
        singleLine = true,
        placeholder = { 
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ) 
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 12.dp)
            .onFocusChanged { focusState = it.isFocused }
            .border(
                width = 1.dp,
                color = if (focusState || imeVisible) MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large
            ),
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = if (focusState || textFieldValue.isNotEmpty()) 
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun MessagesContent(
    messages: List<Message>,
    isLoading: Boolean,
    hasMoreMessages: Boolean,
    onLoadMore: () -> Unit,
    currentFolder: MessageFolder
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(listState) {
        snapshotFlow {
            if (listState.layoutInfo.totalItemsCount == 0) return@snapshotFlow false
            
            val lastVisibleItem = listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size
            val threshold = messages.size - 2
            lastVisibleItem > threshold
        }
        .collect { shouldLoadMore ->
            if (shouldLoadMore && !isLoading && hasMoreMessages) {
                onLoadMore()
            }
        }
    }

    if (messages.isEmpty() && !isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Сообщений не найдено",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
        ) {
            items(
                items = messages,
                key = { it.id ?: it.hashCode().toString() }
            ) { message ->
                MessageItem(
                    message = message,
                    currentFolder = currentFolder
                )
            }
            
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: Message,
    currentFolder: MessageFolder = MessageFolder.INBOX
) {
    val (date, _) = formatDateTime(message.date)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {
            
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (message.unread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                )
            } else {
                if (currentFolder == MessageFolder.INBOX) {
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = when (currentFolder) {
                        MessageFolder.INBOX -> buildString {
                            message.userFrom?.let { user ->
                                append(user.lastName ?: "")
                                if (!user.firstName.isNullOrBlank()) {
                                    if (isNotEmpty()) append(" ")
                                    append("${user.firstName[0]}.")
                                }

                                if (!user.middleName.isNullOrBlank()) {
                                    if (isNotEmpty()) append(" ")
                                    append("${user.middleName[0]}.")
                                }
                            } ?: append("Unknown")
                        }

                        MessageFolder.SENT -> buildString {
                            message.usersTo?.get(0)?.let { user ->
                                append(user.lastName ?: "")
                                if (!user.firstName.isNullOrBlank()) {
                                    if (isNotEmpty()) append(" ")
                                    append("${user.firstName[0]}.")
                                }

                                if (!user.middleName.isNullOrBlank()) {
                                    if (isNotEmpty()) append(" ")
                                    append("${user.middleName[0]}.")
                                }
                            } ?: append("Unknown")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = message.subject,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (currentFolder == MessageFolder.SENT) {
                    message.shortText?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (message.withFiles || message.withResources) {
                    if (message.withFiles) {
                        Icon(
                            painter = painterResource(id = R.drawable.description_24px),
                            contentDescription = "Has attachments",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (message.withResources) {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = "Has resources",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

