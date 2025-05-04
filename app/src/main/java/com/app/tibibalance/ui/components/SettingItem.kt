/* ui/components/SettingItem.kt */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun SettingItem(
    leadingIcon   : @Composable () -> Unit,
    text          : String,
    trailing      : (@Composable () -> Unit)? = null,
    onClick       : (() -> Unit)? = null,
    containerColor: Color = Color.White,
    cornerRadius  : Dp = 16.dp,
    modifier      : Modifier = Modifier
) {
    Surface(
        color  = containerColor,
        shape  = RoundedCornerShape(cornerRadius),   // ✅ radio en Dp
        shadowElevation = 1.dp,
        modifier = modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) { leadingIcon() }

            Spacer(Modifier.width(16.dp))

            Description(
                text = text,
                modifier = Modifier.weight(1f)
            )

            trailing?.invoke()
        }
    }
}
