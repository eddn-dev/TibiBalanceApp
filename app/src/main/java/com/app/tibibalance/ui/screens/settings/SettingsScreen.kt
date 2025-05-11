/* ui/screens/settings/SettingsScreen.kt */
package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.UserProfile
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.DangerButton
import com.app.tibibalance.ui.components.texts.Title
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    state          : SettingsUiState,
    onNavigateUp   : () -> Unit,
    onEditPersonal : () -> Unit,
    onDevices      : () -> Unit,
    onAchievements : () -> Unit,
    onSignOut      : () -> Unit,
    onDelete       : () -> Unit,
    onNotis        : () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showDeleteDialog = remember { mutableStateOf(false) }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Eliminar cuenta") },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog.value = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(state) {
        if (state is SettingsUiState.Error)
            scope.launch { snackbar.showSnackbar(state.message) }
    }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        when (state) {
            SettingsUiState.Loading,
            SettingsUiState.SignedOut -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            is SettingsUiState.Ready -> ReadyContent(
                profile        = state.profile,
                onEditPersonal = onEditPersonal,
                onDelete       = { showDeleteDialog.value = true },
                onDevices      = onDevices,
                onAchievements = onAchievements,
                onSignOut      = onSignOut,
                onNotis        = onNotis
            )

            is SettingsUiState.Error -> {}
        }

        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ReadyContent(
    profile        : UserProfile,
    onEditPersonal : () -> Unit,
    onDelete       : () -> Unit,
    onDevices      : () -> Unit,
    onAchievements : () -> Unit,
    onSignOut      : () -> Unit,
    onNotis        : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profile.photoUrl ?: R.drawable.imagenprueba)
                .crossfade(true)
                .build(),
            contentDescription = "Foto de perfil de ${profile.userName}",
            modifier = Modifier.size(112.dp)
        )
        Title(profile.userName ?: "Sin nombre")

        FormContainer(
            backgroundColor = Color(0xFFD8EAF1),
            contentPadding = PaddingValues(20.dp)
        ) {
            SettingItem(
                leadingIcon = { Icon24(Icons.AutoMirrored.Filled.ListAlt) },
                text = "Editar información personal",
                onClick = onEditPersonal
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.Person) },
                text = "Administrar dispositivos",
                onClick = onDevices
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.NotificationsNone) },
                text = "Configurar notificaciones",
                onClick = onNotis
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.Visibility) },
                text = "Ver logros",
                onClick = onAchievements
            )
        }

        FormContainer(
            backgroundColor = Color(0xFFFFEAEA),
            contentPadding = PaddingValues(20.dp)
        ) {
            DangerButton("Cerrar sesión", onSignOut)
            Spacer(Modifier.height(12.dp))
            DangerButton(
                text = "Eliminar cuenta",
                onClick = onDelete,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun Icon24(icon: androidx.compose.ui.graphics.vector.ImageVector) =
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color(0xFF3EA8FE),
        modifier = Modifier.size(24.dp)
    )
