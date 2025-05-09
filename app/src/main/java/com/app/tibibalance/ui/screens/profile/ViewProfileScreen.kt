/**
 * @file    ViewProfileScreen.kt
 * @ingroup ui_screens_profile // Grupo para pantallas relacionadas con el perfil de usuario
 * @brief   Define el [Composable] para mostrar la información del perfil del usuario.
 *
 * @details Este archivo contiene la implementación de la interfaz de usuario para la pantalla
 * de visualización del perfil. Actualmente, utiliza datos de ejemplo (hardcoded) para
 * mostrar la foto de perfil, nombre, fecha de nacimiento y correo electrónico.
 * Incluye botones para "Editar perfil" y "Cerrar sesión", cuyas acciones
 * están marcadas como TODO.
 *
 * La pantalla se presenta con un fondo degradado y el contenido principal dentro de un
 * [FormContainer] para mantener una estética consistente.
 *
 * **Nota de Desarrollo:**
 * En una implementación completa, esta pantalla debería:
 * - Observar un ViewModel (e.g., `ViewProfileViewModel` o uno compartido) para obtener
 * los datos reales del perfil del usuario.
 * - Los campos `InputText` deberían ser de solo lectura o usar componentes `Text`
 * si la intención es solo mostrar información.
 * - Los botones "Editar perfil" y "Cerrar sesión" deberían invocar las funciones
 * correspondientes en el ViewModel para manejar la navegación o la lógica de negocio.
 *
 * @see ProfileContainer Componente para mostrar la imagen de perfil.
 * @see Subtitle Componente para los títulos de cada sección de información.
 * @see InputText Componente utilizado para mostrar los datos del perfil (actualmente editable, debería ser de solo lectura).
 * @see SecondaryButton Botones para las acciones de editar perfil y cerrar sesión.
 * @see FormContainer Componente que envuelve el contenido del perfil.
 */
// src/main/java/com/app/tibibalance/ui/screens/ViewProfileScreen.kt
package com.app.tibibalance.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Subtitle

/**
 * @brief Composable que define la interfaz de usuario para la pantalla de visualización del perfil.
 *
 * @details Muestra la foto de perfil, nombre, fecha de nacimiento y correo electrónico del usuario
 * (actualmente con datos de ejemplo). Proporciona botones para editar el perfil y cerrar sesión.
 * El contenido es desplazable y se presenta con un fondo degradado.
 */
@Composable
fun ViewProfileScreen() {
    // Define el fondo degradado para la pantalla.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor principal que ocupa toda la pantalla.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient) // Aplica el fondo degradado.
    ) {
        // Columna principal para el contenido, permite desplazamiento vertical.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita el scroll.
                .padding(horizontal = 16.dp, vertical = 24.dp), // Padding general.
            horizontalAlignment = Alignment.CenterHorizontally // Centra elementos horizontalmente.
        ) {
            Spacer(Modifier.height(10.dp)) // Espacio superior.

            // Contenedor estilizado para los elementos del perfil.
            FormContainer {
                // Muestra la imagen de perfil.
                ProfileContainer(
                    imageResId         = R.drawable.imagenprueba, // Recurso de imagen de ejemplo.
                    size               = 110.dp,
                    contentDescription = "Foto de perfil"
                )
                Spacer(Modifier.height(2.dp))

                // Muestra el nombre del usuario.
                Subtitle(text = "Nora Soto") // Nombre de ejemplo.

                Spacer(Modifier.height(20.dp))

                // Sección para la fecha de nacimiento.
                Subtitle(text = "Fecha de nacimiento:")
                InputText(
                    value = "29/07/2004", // Fecha de ejemplo.
                    onValueChange = {}, // No se espera cambio, debería ser solo lectura.
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                    // readOnly = true // Sería apropiado si InputText lo soportara directamente
                )

                Spacer(Modifier.height(10.dp))

                // Sección para el correo electrónico.
                Subtitle(text = "Correo electrónico:")
                InputText(
                    value = "norasoto5@gmail.com", // Correo de ejemplo.
                    onValueChange = {}, // No se espera cambio, debería ser solo lectura.
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                    // readOnly = true
                )

                Spacer(Modifier.height(22.dp))

                // Fila para los botones de acción.
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp) // Espacio entre botones.
                ) {
                    // Botón para editar perfil.
                    SecondaryButton(
                        text = "Editar perfil",
                        onClick = { /* TODO: Navegar a EditProfileScreen o activar modo edición */ },
                        modifier = Modifier.weight(1f) // Ocupa espacio flexible.
                    )
                    // Botón para cerrar sesión.
                    SecondaryButton(
                        text = "Cerrar sesión",
                        onClick = { /* TODO: Implementar lógica de cierre de sesión */ },
                        modifier = Modifier
                            .width(150.dp) // Ancho fijo para este botón.
                    )
                }
            }
        }
    }
}

/**
 * @brief Previsualización del [Composable] [ViewProfileScreen].
 * @details Muestra cómo se vería la pantalla con los datos de ejemplo.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewViewProfileScreen() {
    // Envuelve en MaterialTheme para aplicar estilos si los componentes internos los usan.
    // TibiBalanceTheme { // O el tema específico de la app
    ViewProfileScreen()
    // }
}