package com.app.tibibalance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.app.tibibalance.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale


@Composable
fun Header(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    profileImage: Painter? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
                    .size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }

        profileImage?.let {
            Image(
                painter = it,
                contentDescription = "Imagen de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterEnd)
            )
        }


        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

//Este preview sirve para probar cuando hay flecha de retroceso y foto de perfil
@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    // Usar una imagen local ficticia. Debes tener una imagen con el nombre "profile_sample" en res/drawable
    val dummyProfile = painterResource(id = R.drawable.profile_sample)

    MaterialTheme {
        Header(
            title = "Editar Hábito",
            showBackButton = true,
            onBackClick = { },
            profileImage = dummyProfile
        )
    }
}

//Este preview sirve para probar cuando NO hay foto de perfil ni flecha de retroceso
@Preview(showBackground = true)
@Composable
fun HeaderPreviewSinBackNiFoto() {
    MaterialTheme {
        Header(
            title = "Editar Hábito",
            showBackButton = false,
            profileImage = null
        )
    }
}
