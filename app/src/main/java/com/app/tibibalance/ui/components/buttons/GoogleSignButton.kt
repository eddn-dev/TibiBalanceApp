/**
 * @file    GoogleSignButton.kt
 * @ingroup ui_component_button // Grupo específico para componentes de botón
 * @brief   Define un [Composable] para un botón específico de inicio de sesión/registro con Google.
 *
 * @details Este archivo contiene el [Composable] `GoogleSignButton`, que muestra un
 * botón con estilo delineado ([OutlinedButton]), el logotipo oficial de Google
 * (obtenido de los recursos drawable) y un texto configurable.
 *
 * Está diseñado para ser utilizado en las pantallas de autenticación (login/signup)
 * para iniciar el flujo de autenticación mediante Google Identity Services (GIS) o
 * Google Sign-In. La acción específica se define en el callback `onClick`.
 *
 * @note
 * - Utiliza un recurso drawable (`R.drawable.ic_google_logo`) para el icono. Asegúrate
 * de que este recurso exista en tu proyecto.
 * - Los colores del borde y del texto, así como el fondo transparente, están
 * definidos explícitamente y no dependen del tema Material 3.
 * - Tiene dimensiones fijas (ancho de 250dp, altura de 40dp).
 *
 * @see OutlinedButton Componente base de Material 3 utilizado.
 * @see Image Componente para mostrar el logo.
 * @see painterResource Función para cargar el recurso drawable.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource // Importar si se usa para contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R // Importar la clase R del proyecto

/**
 * @brief Un [Composable] que muestra un botón estándar para "Iniciar sesión con Google".
 *
 * @details Renderiza un [OutlinedButton] con un borde negro, fondo transparente,
 * el icono del logo de Google y un texto. El contenido (icono y texto) se alinea
 * horizontalmente en el centro del botón. Tiene un tamaño predefinido.
 *
 * @param text El [String] que se muestra junto al logo de Google. Por defecto es
 * "Continuar con Google".
 * @param onClick La función lambda que se ejecuta cuando el usuario pulsa el botón.
 * Esta lambda debería iniciar el flujo de autenticación de Google.
 * @param modifier Un [Modifier] opcional para aplicar personalizaciones de layout
 * adicionales desde el exterior. Por defecto, no aplica modificadores extra
 * más allá del tamaño fijo interno.
 */
@Composable
fun GoogleSignButton(
    text: String = "Continuar con Google", // Texto por defecto
    onClick: () -> Unit,
    modifier: Modifier = Modifier // Modificador opcional
) {
    OutlinedButton(
        onClick = onClick, // Acción al pulsar
        modifier = modifier
            .width(250.dp) // Ancho fijo
            .height(48.dp), // Altura fija (ajustada a 48dp)
        shape = RoundedCornerShape(12.dp), // Bordes redondeados (ajustado a 12dp)
        border = BorderStroke(1.dp, Color.Gray), // Borde gris (más suave que negro)
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent, // Fondo transparente
            contentColor = Color.Black // Color del contenido (texto)
        )
    ) {
        // Fila para alinear icono y texto
        Row(
            verticalAlignment = Alignment.CenterVertically, // Centrado vertical
            horizontalArrangement = Arrangement.Center // Centrado horizontal (dentro del espacio del botón)
        ) {
            // Icono de Google
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo), // Carga el logo desde drawable
                // TODO: Considerar usar stringResource(R.string.google_logo_description)
                contentDescription = "Google logo", // Descripción para accesibilidad
                modifier = Modifier.size(24.dp) // Tamaño del icono
            )
            // Espacio entre icono y texto
            Spacer(modifier = Modifier.width(8.dp))
            // Texto del botón
            Text(
                text = text,
                // El color se hereda del contentColor definido en ButtonDefaults
                style = MaterialTheme.typography.labelLarge // Usar estilo de texto apropiado
            )
        }
    }
}

/**
 * @brief Previsualización Composable para [GoogleSignButton].
 * @details Muestra el botón con su texto por defecto en el panel de previsualización
 * de Android Studio, envuelto en [MaterialTheme] para aplicar estilos de texto.
 */
@Preview(showBackground = true, name = "Google Sign Button Preview")
@Composable
private fun GoogleSignButtonPreview() {
    MaterialTheme { // Necesario para MaterialTheme.typography
        GoogleSignButton(onClick = {}) // Llama con texto por defecto y acción vacía
    }
}
