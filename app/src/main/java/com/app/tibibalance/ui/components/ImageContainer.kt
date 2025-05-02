package com.app.tibibalance.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R


@Suppress("UNUSED_FUNCTION")
@Composable
fun ImageContainer(
    imageResId: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Int = 12,
    backgroundColor: Color = Color.Transparent,
    contentScale: ContentScale = ContentScale.Crop
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview
@Composable
fun ImageContainerPreview() {
    ImageContainer(
        imageResId = R.drawable.imagenprueba,
        contentDescription = "ImagenPrueba",
        modifier = Modifier.size(100.dp)
    )
}