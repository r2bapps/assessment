package com.r2b.apps.xuser.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.common.RemoteImage
import com.r2b.apps.xuser.ui.common.extensions.localizedMessage

/**
 * Displays a searchable, paginated list of users with retry and removal actions.
 *
 * Accessibility: labels search and loading states, exposes row actions to assistive technology,
 * and permits user text to wrap, following WCAG AA-oriented practices.
 *
 * Edge-to-edge support: applies safe-drawing insets so list controls and rows avoid system bars
 * while the app surface can draw behind them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    imageLoader: ImageLoader,
    onUserSelected: (Int) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users = uiState.pagingData.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val pendingRemoval = uiState.pendingRemoval
    val loadingUsersLabel = stringResource(R.string.loading_users)
    val loadingMoreUsersLabel = stringResource(R.string.loading_more_users)
    val isAppending = users.loadState.append is LoadState.Loading
    val isRefreshingWithContent =
        users.loadState.mediator?.refresh is LoadState.Loading && users.itemCount > 0
    val progressLabel = if (isRefreshingWithContent) loadingUsersLabel else loadingMoreUsersLabel
    val searchStateDescription = stringResource(
        if (uiState.searchExpanded) R.string.search_expanded else R.string.search_collapsed,
    )

    LaunchedEffect(pendingRemoval?.id) {
        val user = pendingRemoval ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = context.getString(R.string.confirm_undo),
            actionLabel = context.getString(R.string.undo_removal),
            duration = SnackbarDuration.Short,
        )
        viewModel.onAction(
            if (result == SnackbarResult.Dismissed) {
                UserListAction.ConfirmRemoval
            } else {
                UserListAction.UndoRemoval
            },
        )
    }

    LaunchedEffect(uiState.error) {
        val error = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(
            error.localizedMessage(context) ?: context.getString(R.string.unknown_error),
        )
        viewModel.onAction(UserListAction.ErrorShown)
    }

    val refreshError = users.loadState.refresh as? LoadState.Error
    LaunchedEffect(refreshError, users.itemCount) {
        if (refreshError != null && users.itemCount > 0) {
            val result = snackbarHostState.showSnackbar(
                message = refreshError.error.localizedMessage(context)
                    ?: context.getString(R.string.unknown_error),
                actionLabel = context.getString(R.string.retry),
                duration = SnackbarDuration.Indefinite,
            )
            if (result == SnackbarResult.ActionPerformed) users.retry()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_launcher_image),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .size(32.dp)
                                .clip(CircleShape),
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.onAction(UserListAction.ToggleSearch) },
                            modifier = Modifier.semantics {
                                stateDescription = searchStateDescription
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_filter),
                                contentDescription = stringResource(R.string.action_filter),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                ) {
                    if (isAppending || isRefreshingWithContent) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .semantics {
                                    contentDescription = progressLabel
                                },
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            if (uiState.searchExpanded) {
                OutlinedTextField(
                    value = uiState.searchText,
                    onValueChange = {
                        viewModel.onAction(UserListAction.SearchQueryChanged(it))
                    },
                    label = { Text(stringResource(R.string.search_users)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text(stringResource(R.string.search_filter_hint)) },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.onAction(UserListAction.CloseSearch)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.close_filter),
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    },
                )
            }

            when {
                users.loadState.refresh is LoadState.Loading && users.itemCount == 0 -> {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.semantics {
                                contentDescription = loadingUsersLabel
                            },
                        )
                    }
                }
                refreshError != null && users.itemCount == 0 -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        MessageWithRetry(
                            message = refreshError.error.localizedMessage(context)
                                ?: stringResource(R.string.unknown_error),
                            onRetry = users::retry,
                        )
                    }
                }
                users.loadState.refresh is LoadState.NotLoading && users.itemCount == 0 -> {
                    Box(
                        Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.empty_user_list))
                    }
                }
                else -> {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(
                            count = users.itemCount,
                            key = { index -> users[index]?.id ?: "placeholder-$index" },
                        ) { index ->
                            val user = users[index]
                            if (user != null) {
                                UserRow(
                                    user = user,
                                    imageLoader = imageLoader,
                                    removalPending = pendingRemoval != null,
                                    onClick = { onUserSelected(user.id) },
                                    onSwiped = {
                                        viewModel.onAction(UserListAction.RemovalRequested(user))
                                    },
                                )
                            } else {
                                UserRowPlaceholder()
                            }
                        }

                        when (val append = users.loadState.append) {
                            LoadState.Loading -> item(key = "append-loading") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.semantics {
                                            contentDescription = loadingMoreUsersLabel
                                        },
                                        strokeWidth = 2.dp,
                                    )
                                    Text(
                                        text = loadingMoreUsersLabel,
                                        modifier = Modifier.padding(start = 12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                            is LoadState.Error -> item(key = "append-error") {
                                MessageWithRetry(
                                    message = append.error.localizedMessage(context)
                                        ?: stringResource(R.string.unknown_error),
                                    onRetry = users::retry,
                                )
                            }
                            is LoadState.NotLoading -> Unit
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserRow(
    user: User,
    imageLoader: ImageLoader,
    removalPending: Boolean,
    onClick: () -> Unit,
    onSwiped: () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val removeActionLabel = stringResource(
        R.string.remove_user_accessibility,
        "${user.name} ${user.surname}".trim(),
    )
    LaunchedEffect(dismissState.currentValue, removalPending) {
        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
            if (!removalPending) onSwiped()
            dismissState.reset()
        }
    }
    SwipeToDismissBox(
        state = dismissState,
        gesturesEnabled = !removalPending,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.Button, onClick = onClick)
                .semantics {
                    customActions = if (removalPending) emptyList() else {
                        listOf(
                            CustomAccessibilityAction(label = removeActionLabel) {
                                if (!removalPending) onSwiped()
                                true
                            },
                        )
                    }
                },
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                RemoteImage(
                    model = user.picture.takeIf(String::isNotBlank),
                    imageLoader = imageLoader.getImageLoader(),
                    placeholderRes = R.drawable.ic_profile,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${user.name} ${user.surname}".trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (user.email.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_email),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = user.email,
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    if (user.phone.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_phone),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = user.phone,
                                modifier = Modifier.padding(start = 6.dp),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserRowPlaceholder() {
    val loadingLabel = stringResource(R.string.loading_users)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = loadingLabel
            },
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
        }
    }
}

@Composable
private fun MessageWithRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
) {
    Column(
        modifier = modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 12.dp),
        ) {
            Text(stringResource(R.string.retry))
        }
    }
}
