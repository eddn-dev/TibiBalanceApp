package com.app.tibibalance.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Encabezado reutilizable para pantallas con o sin botón “Atrás”
 * y (opcional) avatar de usuario.
 *
 * @param title            Texto centrado.
 * @param showBackButton   Si `true`, muestra la flecha (nav icon).
 * @param onBackClick      Callback de la flecha.
 * @param profileImage     Si se pasa un [Painter], se muestra a la derecha.
 * @param onProfileClick   Callback del avatar (si existe).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    title: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    profileImage: Painter? = null,
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                maxLines = 1
            )
        },
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás"
                    )
                }
            }
        } else {
            {}
        },
        actions = {
            profileImage?.let {
                IconButton(onClick = onProfileClick) {
                    Image(
                        painter = it,
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
    )
}
