package com.r2b.apps.xuser.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.r2b.apps.xuser.ui.previewImageLoader
import com.r2b.apps.xuser.ui.previewUserListViewModel
import com.r2b.apps.xuser.ui.theme.XuserTheme

@Preview(name = "User list", showBackground = true)
@Composable
private fun UserListScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserListViewModel() }

    XuserTheme {
        UserListScreen(
            imageLoader = imageLoader,
            onUserSelected = {},
            viewModel = viewModel,
        )
    }
}

@Preview(name = "User list - empty", showBackground = true)
@Composable
private fun EmptyUserListScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserListViewModel(emptyList()) }

    XuserTheme {
        UserListScreen(
            imageLoader = imageLoader,
            onUserSelected = {},
            viewModel = viewModel,
        )
    }
}

@Preview(name = "User list - API error", showBackground = true)
@Composable
private fun UserListErrorScreenPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val viewModel = remember { previewUserListViewModel(failOnList = true) }

    XuserTheme {
        UserListScreen(
            imageLoader = imageLoader,
            onUserSelected = {},
            viewModel = viewModel,
        )
    }
}
