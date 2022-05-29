package com.r2b.apps.xuser.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.r2b.apps.xuser.ui.theme.XuserTheme

@Preview(name = "App navigation", showBackground = true)
@Composable
private fun XuserNavigationPreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { previewImageLoader(context) }
    val listViewModel = remember { previewUserListViewModel() }
    val detailViewModel = remember { previewUserDetailViewModel() }

    XuserTheme {
        XuserNavigationWithViewModels(
            imageLoader = imageLoader,
            listViewModel = listViewModel,
            detailViewModel = detailViewModel,
        )
    }
}
