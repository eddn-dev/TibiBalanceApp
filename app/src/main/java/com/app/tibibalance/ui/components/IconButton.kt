// ui/components/IconButton.kt
package com.app.tibibalance.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R

/**
 * Un botón circular que muestra un drawable dentro de tu ImageContainer,
 * con tamaño fijo, padding interior y click handler.
 */
@Composable
fun IconButton(
    @DrawableRes resId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 35.dp,
    contentPadding: Dp = 2.dp,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        ImageContainer(
            resId              = resId,
            contentDescription = contentDescription,
            modifier           = Modifier.size(size - contentPadding * 2)
        )
    }
}

@Preview(showBackground = true )
@Composable
private fun PreviewIconButton() {
    IconButton(
        resId              = R.drawable.iconbellswitch_off,
        contentDescription = "Campana apagada",
        onClick            = { /*…*/ }
    )

}
