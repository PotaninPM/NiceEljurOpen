package com.team.feature_messages.presentation

import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.feature_messages.presentation.viewmodel.MessagesUiState
import com.team.feature_messages.presentation.viewmodel.MessagesViewModel

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val authToken = remember {
        context.getSharedPreferences("niceeljur", Context.MODE_PRIVATE)
            .getString("jwt_token", "") ?: ""
    }
    
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialMessages(authToken)
    }

    Scaffold(
        topBar = {
            MessagesTopBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { query -> viewModel.onSearchQueryChanged(authToken, query) },
                unreadOnly = uiState.unreadOnly,
                onUnreadOnlyChange = { unreadOnly -> viewModel.onUnreadOnlyChanged(authToken, unreadOnly) }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            MessagesTabs(
                currentFolder = uiState.currentFolder,
                onFolderSelected = { folder -> viewModel.onFolderSelected(authToken, folder) }
            )
            
            MessagesContent(
                uiState = uiState,
                onLoadMore = { viewModel.loadNextPage(authToken) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessagesTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    unreadOnly: Boolean,
    onUnreadOnlyChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = {
            CustomSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                placeholder = "Search messages...",
                onSearch = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        },
        actions = {
            IconToggleButton(
                checked = unreadOnly,
                onCheckedChange = onUnreadOnlyChange
            ) {
                Icon(
                    imageVector = if (unreadOnly) Icons.Default.Info else Icons.Default.Info,
                    contentDescription = "Toggle unread only"
                )
            }
        }
    )
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: () -> Unit = {}
) {
    var focused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = if (focused) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty() && !focused) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focused = it.isFocused }
                        .padding(vertical = 8.dp)
                        .imeAction(ImeAction.Search, onSearch)
                )
            }
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun Modifier.imeAction(
    imeAction: ImeAction,
    onAction: () -> Unit
): Modifier = this.then(
    Modifier.onKeyEvent {
        if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
            onAction()
            true
        } else false
    }
)


@Composable
private fun MessagesTabs(
    currentFolder: MessageFolder,
    onFolderSelected: (MessageFolder) -> Unit
) {
    TabRow(
        selectedTabIndex = currentFolder.ordinal
    ) {
        MessageFolder.entries.forEach { folder ->
            Tab(
                selected = folder == currentFolder,
                onClick = { onFolderSelected(folder) },
                text = { Text(folder.name.lowercase().replaceFirstChar { it.uppercase() }) },
                icon = {
                    Icon(
                        if (folder == MessageFolder.INBOX) Icons.Default.MailOutline else Icons.Default.Send,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun MessagesContent(
    uiState: MessagesUiState,
    onLoadMore: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading && uiState.messages.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageItem(message = message)
                }
                
                item {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.messages.size < uiState.totalMessages) {
                        Button(
                            onClick = onLoadMore,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text("Load More")
                        }
                    }
                }
            }
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageItem(message: Message) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle message click */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.subject ?: "No subject",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (message.unread) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Text("NEW")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildString {
                        message.userFrom?.let { user ->
                            append(user.lastName ?: "")
                            if (!user.firstName.isNullOrBlank()) {
                                if (isNotEmpty()) append(" ")
                                append(user.firstName)
                            }
                        } ?: append("Unknown")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = message.date ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (message.withFiles || message.withResources) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (message.withFiles) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Has attachments",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (message.withResources) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Has resources",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
} 