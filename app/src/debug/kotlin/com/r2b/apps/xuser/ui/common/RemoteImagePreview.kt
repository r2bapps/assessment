package com.r2b.apps.xuser.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.r2b.apps.xuser.R
import com.r2b.apps.xuser.ui.theme.XuserTheme

@Preview(name = "Remote image placeholder", showBackground = true)
@Composable
private fun RemoteImagePreview() {
    val context = LocalContext.current
    val imageLoader = remember(context) { coil.ImageLoader.Builder(context).build() }

    XuserTheme {
        RemoteImage(
            model = null,
            imageLoader = imageLoader,
            placeholderRes = R.drawable.ic_profile,
            modifier = Modifier.size(96.dp),
        )
    }
}
