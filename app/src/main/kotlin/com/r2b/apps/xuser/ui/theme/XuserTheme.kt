package com.r2b.apps.xuser.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.r2b.apps.xuser.R

/**
 * Applies the app's flavor-specific light or dark Material theme.
 *
 * Accessibility: uses flavor palettes with primary-color contrast checked against the relevant
 * theme background, following WCAG AA contrast guidance.
 */
@Composable
fun XuserTheme(content: @Composable () -> Unit) {
    val colorScheme = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = colorResource(R.color.primaryColorDark),
            onPrimary = Color.Black,
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.primaryColor),
            onPrimary = Color.White,
        )
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
