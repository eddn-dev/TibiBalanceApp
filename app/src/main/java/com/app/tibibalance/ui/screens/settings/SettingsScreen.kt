// src/main/java/com/app/tibibalance/ui/screens/settings/SettingsScreen.kt
package com.app.tibibalance.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.UserProfile
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.RoundedIconButton
import com.app.tibibalance.ui.components.SettingItem
import com.app.tibibalance.ui.components.buttons.DangerButton
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.screens.conection.ConnectedDeviceScreen
import com.app.tibibalance.ui.screens.conection.ConnectedViewModel

/**
 * @file    SettingsScreen.kt
 * @ingroup ui_screens_settings
 * @brief   Pantalla de Ajustes de la aplicación.
 *
 * @details
 * Muestra:
 *  - Información de perfil (foto, nombre).
 *  - Opciones de edición de perfil, notificaciones, logros, cerrar o eliminar cuenta.
 *  - Sección inline “Administrar dispositivos” que instancia
 *    [ConnectedDeviceScreen] para mostrar estado de reloj y métricas.
 *
 * Usa [RoundedIconButton] para el botón de retroceso y
 * [ConnectedViewModel] para gestionar la conexión y carga de métricas.
 *
 * @see ConnectedDeviceScreen
 * @see RoundedIconButton
 * @see ConnectedViewModel
 */
@Composable
fun SettingsScreen(
    state          : SettingsUiState,
    onEditPersonal : () -> Unit,
    onAchievements : () -> Unit,
    onSignOut      : () -> Unit,
    onDelete       : () -> Unit,
    onNotis        : () -> Unit
) {
    val snackbar = remember { SnackbarHostState() }

    // Mostrar Snackbar en caso de error de estado
    LaunchedEffect(state) {
        if (state is SettingsUiState.Error) {
            snackbar.showSnackbar(state.message)
        }
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
            SettingsUiState.SignedOut -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is SettingsUiState.Ready -> {
                ReadyContent(
                    profile        = state.profile,
                    onEditPersonal = onEditPersonal,
                    onAchievements = onAchievements,
                    onSignOut      = onSignOut,
                    onDelete       = onDelete,
                    onNotis        = onNotis
                )
            }
            is SettingsUiState.Error -> {
                // Snackbar ya mostrado
            }
        }
        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * @brief Content principal de la pantalla de Ajustes cuando el estado está listo.
 * @param profile        Perfil de usuario con `photoUrl` y `userName`.
 * @param onEditPersonal Callback para editar información personal.
 * @param onAchievements Callback para ver logros.
 * @param onSignOut      Callback para cerrar sesión.
 * @param onDelete       Callback para eliminar cuenta.
 * @param onNotis        Callback para configurar notificaciones.
 */
@Composable
private fun ReadyContent(
    profile        : UserProfile,
    onEditPersonal : () -> Unit,
    onAchievements : () -> Unit,
    onSignOut      : () -> Unit,
    onDelete       : () -> Unit,
    onNotis        : () -> Unit
) {
    var showDevices by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Foto de perfil y nombre
        AsyncImage(
            model = profile.photoUrl ?: R.drawable.imagenprueba,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
        )
        Title(text = profile.userName ?: "Sin nombre")

        if (!showDevices) {
            // Opciones de Ajustes
            FormContainer(
                backgroundColor = Color(0xFFD8EAF1),
                contentPadding = PaddingValues(20.dp)
            ) {
                SettingItem(
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
                    text = "Editar información personal",
                    onClick = onEditPersonal
                )
                SettingItem(
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    text = "Administrar dispositivos",
                    onClick = { showDevices = true }
                )
                SettingItem(
                    leadingIcon = { Icon(Icons.Default.NotificationsNone, contentDescription = null) },
                    text = "Configurar notificaciones",
                    onClick = onNotis
                )
                SettingItem(
                    leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                    text = "Ver logros",
                    onClick = onAchievements
                )
            }
            FormContainer(
                backgroundColor = Color(0xFFFFEAEA),
                contentPadding = PaddingValues(20.dp)
            ) {
                DangerButton(text = "Cerrar sesión", onClick = onSignOut)
                Spacer(modifier = Modifier.height(12.dp))
                DangerButton(text = "Eliminar cuenta", onClick = onDelete)
            }
        } else {
            // Inline: Administrar dispositivos
            val vm: ConnectedViewModel = hiltViewModel()
            val metrics    by vm.latest.collectAsState()
            val isConnected by vm.isConnected.collectAsState()
            val isLoading  by vm.isLoading.collectAsState()
            val isError    by vm.isError.collectAsState()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RoundedIconButton(
                    onClick = { showDevices = false },
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier.size(40.dp),
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Administrar dispositivos", fontSize = 20.sp)
            }

            ConnectedDeviceScreen(
                metrics        = metrics,
                isConnected    = isConnected,
                isLoading      = isLoading,
                isError        = isError,
                onRefreshClick = {
                    vm.refreshConnection()
                    vm.refreshMetrics()
                }
            )
        }
    }
}

