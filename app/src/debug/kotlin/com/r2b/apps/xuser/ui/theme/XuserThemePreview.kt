package com.r2b.apps.xuser.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "XUser theme", showBackground = true)
@Composable
private fun XuserThemePreview() {
    XuserTheme {
        Surface {
            Text("Sample content", modifier = Modifier.padding(16.dp))
        }
    }
}
