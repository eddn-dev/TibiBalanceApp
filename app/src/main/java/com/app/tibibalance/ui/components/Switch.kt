// file: ui/components/SwitchToggle.kt
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SwitchToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .width(50.dp)
            .height(35.dp),
        colors = SwitchDefaults.colors(
            checkedThumbColor    = Color.White,
            uncheckedThumbColor  = Color.White,
            checkedTrackColor    = Color(0xFF458BAE),
            uncheckedTrackColor  = Color(0xFFB0BEC5)
        )
    )
}

@Preview(showBackground = true, widthDp = 120)
@Composable
fun SwitchTogglePreview() {
    Row(
        Modifier
            .padding(16.dp)
            .width(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var off by remember { mutableStateOf(false) }
        SwitchToggle(checked = off, onCheckedChange = { off = it })
        var on by remember { mutableStateOf(true) }
        SwitchToggle(checked = on, onCheckedChange = { on = it })
    }
}
