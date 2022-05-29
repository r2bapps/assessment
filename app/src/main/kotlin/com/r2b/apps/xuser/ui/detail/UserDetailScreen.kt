package com.r2b.apps.xuser.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.r2b.apps.lib.imageloader.ImageLoader
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.domain.model.User
import com.r2b.apps.xuser.ui.common.RemoteImage
import com.r2b.apps.xuser.ui.common.extensions.localizedMessage
import com.r2b.apps.xuser.ui.common.extensions.safeLocalizedDate

/**
 * Displays a user's details with loading, not-found, and retryable error states.
 *
 * Accessibility: provides labeled navigation and retry controls, announces loading, and allows
 * detail content to wrap and scroll at larger text sizes, following WCAG AA-oriented practices.
 *
 * Edge-to-edge support: applies safe-drawing insets so detail content and navigation avoid system
 * bars while the app surface can draw behind them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    imageLoader: ImageLoader,
    onBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.onAction(UserDetailAction.Load(userId))
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.user_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.navigate_back),
                            modifier = Modifier.padding(4.dp),
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
    ) { contentPadding ->
        when (val state = uiState) {
            UserDetailViewModel.UserUiState.Loading -> {
                val loadingLabel = stringResource(R.string.loading)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.semantics {
                            contentDescription = loadingLabel
                        },
                    )
                }
            }
            is UserDetailViewModel.UserUiState.Content -> {
                UserDetails(
                    user = state.user,
                    imageLoader = imageLoader,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            UserDetailViewModel.UserUiState.NotFound -> {
                MessageWithRetry(
                    message = stringResource(R.string.user_not_found),
                    onRetry = { viewModel.onAction(UserDetailAction.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is UserDetailViewModel.UserUiState.Error -> {
                val context = LocalContext.current
                MessageWithRetry(
                    message = state.error.localizedMessage(context)
                        ?: stringResource(R.string.unknown_error),
                    onRetry = { viewModel.onAction(UserDetailAction.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
        }
    }
}

@Composable
private fun UserDetails(user: User, imageLoader: ImageLoader, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        RemoteImage(
            model = user.picture.takeIf(String::isNotBlank),
            imageLoader = imageLoader.getImageLoader(),
            placeholderRes = R.drawable.ic_camera,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 4.dp),
        )
        UserDetailField(R.string.name_label, user.name)
        UserDetailField(R.string.description_label, user.description)
        UserDetailField(R.string.surname_label, user.surname)
        UserDetailField(R.string.email_label, user.email)
        UserDetailField(R.string.gender_label, user.gender)
        UserDetailField(R.string.address_label, user.location)
        UserDetailField(R.string.registered_label, user.registeredDate.safeLocalizedDate())
    }
}

@Composable
private fun UserDetailField(labelRes: Int, value: String) {
    if (value.isBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun MessageWithRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(stringResource(R.string.retry))
        }
    }
}
