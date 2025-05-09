package com.team.feature_messages.presentation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.team.feature_messages.data.model.Message
import com.team.feature_messages.data.model.MessageFolder
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
                .padding(top = padding.calculateTopPadding())
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
    var selectedFilter by remember { mutableStateOf("All") }

    CustomSearchBar(
        query = searchQuery,
        onQueryChange = {
            onSearchQueryChange(it)
        },
        onSearch = {

        },
        filterOptions = listOf("All", "Unread", "Starred"),
        selectedFilter = selectedFilter,
        onFilterSelected = { selectedFilter = it }
    )
    /*TopAppBar(
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
    )*/
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search…",
    onSearch: () -> Unit = {},
    filterOptions: List<String> = listOf("All", "Unread", "Starred"),
    selectedFilter: String = filterOptions.first(),
    onFilterSelected: (String) -> Unit
) {
    var focusState by remember { mutableStateOf(false) }
    var showFilterPopup by remember { mutableStateOf(false) }
    val filterIconPosition = remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(placeholder) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                disabledTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .onFocusChanged { focusState = it.isFocused }
                .padding(horizontal = 10.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            trailingIcon = {
                Row {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Filter",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .onGloballyPositioned { coords ->
                                filterIconPosition.value = coords.localToWindow(Offset.Zero)
                            }
                            .clickable { showFilterPopup = !showFilterPopup }
                    )
                }
            }
        )

        if (showFilterPopup) {
            Popup(
                offset = IntOffset(
                    x = filterIconPosition.value.x.toInt(),
                    y = (filterIconPosition.value.y + with(LocalDensity.current) { 24.dp.toPx() }).toInt()
                ),
                onDismissRequest = { showFilterPopup = false }
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.width(150.dp)
                ) {
                    Column {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onFilterSelected(option)
                                    showFilterPopup = false
                                }
                            )
                        }
                    }
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

@Composable
private fun MessageItem(message: Message) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
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