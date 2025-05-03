package com.app.tibibalance.ui.components

import androidx.annotation.DrawableRes
import com.app.tibibalance.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ImageContainer(
    @DrawableRes resId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,          // sin gris por defecto
    contentScale: ContentScale = ContentScale.Fit,       // evita recorte
    cornerRadius: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}
