package com.team.feature_messages.presentation

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.common.components.UserInfoTopBar
import com.team.common.components.icons.BellIcon
import com.team.common.components.icons.FilterIcon
import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
import com.team.feature_messages.presentation.viewmodel.MessagesUiState
import com.team.feature_messages.presentation.viewmodel.MessagesViewModel

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadInitialMessages()
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
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { query -> viewModel.onSearchQueryChanged(query) },
                unreadOnly = uiState.unreadOnly,
                onUnreadOnlyChange = { unreadOnly -> viewModel.onUnreadOnlyChanged(unreadOnly) }
            )

            MessagesTabs(
                currentFolder = uiState.currentFolder,
                onFolderSelected = { folder -> viewModel.onFolderSelected(folder) }
            )
            
            MessagesContent(
                uiState = uiState,
                onLoadMore = { viewModel.loadNextPage() }
            )
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
            onQueryChange = {
                onSearchQueryChange(it)
            },
            onSearch = {

            }
        )
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search…",
    onSearch: () -> Unit = {},
) {
    var focusState by remember { mutableStateOf(false) }

    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    val imeVisible = imeBottomPx > 0

    TextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
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
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .padding(horizontal = 12.dp)
            .onFocusChanged { focusState = it.isFocused }
            .border(
                width = 1.dp,
                color = if (imeVisible) MaterialTheme.colorScheme.primary else Color.Gray,
                shape = MaterialTheme.shapes.large
            ),
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        }
    )
}

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
                text = { Text(folder.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun MessagesContent(
    uiState: MessagesUiState,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(listState, uiState) {
        snapshotFlow {
            if (listState.layoutInfo.totalItemsCount == 0) return@snapshotFlow false
            
            val lastVisibleItem = listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size
            val threshold = uiState.messages.size - 2
            lastVisibleItem > threshold
        }
        .collect { shouldLoadMore ->
            if (shouldLoadMore && !uiState.isLoading && uiState.messages.size < uiState.totalMessages) {
                onLoadMore()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (uiState.isLoading && uiState.messages.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 12.dp)
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
                    }
                }
            }
        }

        if (uiState.error != null) {
            Text(
                text = "Непредвиденная ошибка",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun MessageItem(message: Message) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = {

        }
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