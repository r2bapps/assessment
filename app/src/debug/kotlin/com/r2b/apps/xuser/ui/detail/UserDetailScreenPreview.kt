package com.r2b.apps.xuser.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.r2b.apps.xuser.ui.previewImageLoader
import com.r2b.apps.xuser.ui.previewUserDetailViewModel
import com.r2b.apps.xuser.ui.theme.XuserTheme

@Preview(name = "User details", showBackground = true)
@Composable
private fun UserDetailScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserDetailViewModel() }

    XuserTheme {
        UserDetailScreen(
            userId = 1,
            imageLoader = imageLoader,
            onBack = {},
            viewModel = viewModel,
        )
    }
}

@Preview(name = "User details - not found", showBackground = true)
@Composable
private fun UserDetailNotFoundScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserDetailViewModel(user = null) }

    XuserTheme {
        UserDetailScreen(
            userId = 1,
            imageLoader = imageLoader,
            onBack = {},
            viewModel = viewModel,
        )
    }
}

@Preview(name = "User details - error", showBackground = true)
@Composable
private fun UserDetailErrorScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserDetailViewModel(fail = true) }

    XuserTheme {
        UserDetailScreen(
            userId = 1,
            imageLoader = imageLoader,
            onBack = {},
            viewModel = viewModel,
        )
    }
}
