/**
 * @file SettingsScreen.kt
 * @ingroup ui_screens_settings // Grupo para pantallas de configuración
 * @brief Define el [Composable] para la pantalla de Ajustes del usuario.
 *
 * @details Este archivo contiene la implementación de la interfaz de usuario para la
 * pantalla de "Ajustes". La pantalla permite al usuario acceder a diferentes
 * sub-secciones como editar información personal, administrar dispositivos,
 * configurar notificaciones, ver logros, y también ofrece opciones para
 * cerrar sesión o eliminar la cuenta.
 *
 * La pantalla se adapta al estado proporcionado por [SettingsUiState]:
 * - Muestra un indicador de carga ([CircularProgressIndicator]) durante los estados
 * `Loading` o `SignedOut` (este último mientras se redirige).
 * - Renderiza el contenido principal a través de [ReadyContent] cuando el estado es `Ready`,
 * mostrando la foto de perfil, el nombre del usuario, y las diferentes opciones de configuración.
 * - Muestra errores globales a través de un [SnackbarHost].
 *
 * Utiliza componentes reutilizables como [AsyncImage] para la foto de perfil, [Title] para
 * el nombre, [FormContainer] para agrupar opciones, [SettingItem] para cada
 * opción individual, y [DangerButton] para acciones destructivas.
 * El fondo de la pantalla tiene un gradiente corporativo.
 *
 * @see SettingsUiState Define los diferentes estados que puede tener esta pantalla.
 * @see SettingsViewModel ViewModel que gestiona la lógica y el estado de esta pantalla (no directamente usado aquí, sino en `SettingsTab`).
 * @see ReadyContent Composable privado que renderiza la UI cuando el perfil está cargado.
 * @see FormContainer Componente para agrupar las opciones de configuración.
 * @see SettingItem Componente para cada fila de opción.
 * @see DangerButton Componente para los botones de "Cerrar sesión" y "Eliminar cuenta".
 * @see AsyncImage Componente de la librería Coil para cargar imágenes de forma asíncrona.
 */
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


/**
 * @brief Composable principal para la pantalla de Ajustes.
 *
 * @details Este Composable observa el [SettingsUiState] para determinar qué mostrar:
 * un indicador de carga, el contenido principal del perfil y opciones, o un mensaje de error
 * (a través de un Snackbar).
 *
 * @param state El estado actual de la UI de Ajustes ([SettingsUiState]), proporcionado por el ViewModel.
 * @param onNavigateUp Callback para manejar la acción de retroceso (e.g., botón "Atrás" en una TopAppBar, no visible aquí directamente).
 * @param onEditPersonal Callback invocado cuando el usuario pulsa "Editar información personal".
 * @param onDevices Callback invocado cuando el usuario pulsa "Administrar dispositivos".
 * @param onAchievements Callback invocado cuando el usuario pulsa "Ver logros".
 * @param onSignOut Callback invocado cuando el usuario pulsa "Cerrar sesión".
 * @param onDelete Callback invocado cuando el usuario pulsa "Eliminar cuenta".
 * @param onNotis Callback invocado cuando el usuario pulsa "Configurar notificaciones".
 */
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
    // Estado para gestionar los mensajes mostrados en el Snackbar.
    val snackbar = remember { SnackbarHostState() }
    // Scope de corrutina para lanzar la visualización del Snackbar.
    val scope    = rememberCoroutineScope()

    /* Muestra errores globales en Snackbar */
    // Efecto que se lanza cuando el 'state' cambia. Si es un error, muestra el Snackbar.
    LaunchedEffect(state) {
        if (state is SettingsUiState.Error)
            scope.launch { snackbar.showSnackbar(state.message) }
    }

    /* Gradiente de fondo corporativo */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor principal de la pantalla con el fondo degradado.
    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Determina qué contenido mostrar basado en el 'state'.
        when (state) {
            // Si está cargando o se acaba de cerrar sesión, muestra un indicador de progreso.
            SettingsUiState.Loading,
            SettingsUiState.SignedOut -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            // Si el perfil está listo, muestra el contenido principal.
            is SettingsUiState.Ready -> ReadyContent(
                profile        = state.profile,
                onEditPersonal = onEditPersonal,
                onDelete       = onDelete,
                onDevices      = onDevices,
                onAchievements = onAchievements,
                onSignOut      = onSignOut,
                onNotis        = onNotis
            )

            // Si hay un error, ya se ha gestionado para mostrarlo en el Snackbar,
            // por lo que no se muestra nada más aquí.
            is SettingsUiState.Error -> {} // ya mostrado en Snackbar
        }

        // Host para el Snackbar, alineado en la parte inferior central.
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter))
    }
}

/**
 * @brief Composable privado que renderiza el contenido de la pantalla de Ajustes cuando el perfil de usuario está cargado y listo.
 *
 * @details Muestra la imagen de perfil, el nombre del usuario, y dos bloques de opciones:
 * uno para configuraciones generales y otro para acciones de "zona peligrosa" (cerrar sesión, eliminar cuenta).
 * El contenido es desplazable verticalmente.
 *
 * @param profile El objeto [UserProfile] con los datos del usuario.
 * @param onEditPersonal Callback para la acción de editar información personal.
 * @param onDelete Callback para la acción de eliminar cuenta.
 * @param onDevices Callback para la acción de administrar dispositivos.
 * @param onAchievements Callback para la acción de ver logros.
 * @param onSignOut Callback para la acción de cerrar sesión.
 * @param onNotis Callback para la acción de configurar notificaciones.
 */
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
    // Columna principal desplazable para el contenido.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite el desplazamiento vertical.
            // Padding: más en la parte superior para dejar espacio a un posible Header no presente aquí.
            .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Centra los elementos horizontalmente.
        verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio vertical entre grupos de elementos.
    ) {
        /* Avatar y Nombre del Usuario */
        // Carga la imagen de perfil de forma asíncrona usando Coil.
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                // Usa la photoUrl del perfil, o una imagen de prueba como fallback.
                .data(profile.photoUrl ?: R.drawable.imagenprueba)
                .crossfade(true) // Habilita animación de crossfade al cargar.
                .build(),
            contentDescription = "Foto de perfil de ${profile.userName}", // Descripción para accesibilidad.
            modifier = Modifier.size(112.dp) // Tamaño fijo para el avatar.
        )
        // Muestra el nombre del usuario, o "Sin nombre" si es nulo.
        Title(profile.userName ?: "Sin nombre")

        /* Bloque de opciones de configuración generales */
        FormContainer(
            backgroundColor = Color(0xFFD8EAF1), // Color de fondo personalizado para este contenedor.
            contentPadding  = PaddingValues(20.dp) // Padding interno.
        ) {
            SettingItem(
                leadingIcon = { Icon24(Icons.AutoMirrored.Filled.ListAlt) }, // Icono para editar info.
                text        = "Editar información personal",
                onClick     = onEditPersonal // Acción al pulsar.
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.Person) }, // Icono para dispositivos.
                text        = "Administrar dispositivos",
                onClick     = onDevices
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.NotificationsNone) }, // Icono para notificaciones.
                text        = "Configurar notificaciones",
                onClick     = onNotis
            )
            SettingItem(
                leadingIcon = { Icon24(Icons.Default.Visibility) }, // Icono para logros.
                text        = "Ver logros",
                onClick     = onAchievements
            )
        }


        /* Bloque de opciones de "Danger Zone" */
        FormContainer(
            backgroundColor = Color(0xFFFFEAEA), // Color de fondo rojizo para indicar peligro.
            contentPadding  = PaddingValues(20.dp)
        ) {
            DangerButton("Cerrar sesión", onSignOut) // Botón para cerrar sesión.
            Spacer(Modifier.height(12.dp)) // Espacio entre botones.
            DangerButton("Eliminar cuenta", onDelete) // Botón para eliminar cuenta.
        }
    }
}

/**
 * @brief Composable helper para crear un [Icon] con un tamaño y tinte predefinidos.
 * @details Simplifica la creación de iconos consistentes para los [SettingItem].
 *
 * @param icon El [ImageVector] del icono a mostrar.
 */
@Composable
private fun Icon24(icon: androidx.compose.ui.graphics.vector.ImageVector) =
    Icon(
        imageVector = icon,
        contentDescription = null, // Iconos decorativos dentro de SettingItem con texto.
        tint = Color(0xFF3EA8FE), // Tinte azul corporativo.
        modifier = Modifier.size(24.dp) // Tamaño fijo de 24dp.
    )