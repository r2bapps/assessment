package com.r2b.apps.xuser.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.AsyncImage
import com.r2b.apps.xuser.R

/**
 * Loads a remote image and displays a placeholder while loading or when loading fails.
 *
 * Accessibility: treats the image as decorative and exposes a label for its loading indicator,
 * following WCAG AA-oriented accessibility practices.
 */
@Composable
fun RemoteImage(
    model: String?,
    imageLoader: coil.ImageLoader,
    placeholderRes: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val placeholder = painterResource(placeholderRes)
    val loadingLabel = stringResource(R.string.loading)
    var state by remember(model) {
        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
    }

    Box(modifier = modifier) {
        AsyncImage(
            model = model,
            imageLoader = imageLoader,
            placeholder = placeholder,
            error = placeholder,
            fallback = placeholder,
            contentDescription = null,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize(),
            onLoading = { state = it },
            onSuccess = { state = it },
            onError = { state = it },
        )

        if (state is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .semantics {
                            contentDescription = loadingLabel
                        },
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}
