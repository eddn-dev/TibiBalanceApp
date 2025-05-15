/**
 * @file    FormContainer.kt
 * @ingroup ui_component_layout // Grupo para componentes de layout o complejos
 * @brief   Define un [Composable] reutilizable que actúa como contenedor visual para formularios.
 *
 * @details Este archivo contiene el [Composable] `FormContainer`, que utiliza un componente
 * [Card] de Material 3 para agrupar visualmente elementos de un formulario
 * (como campos de texto [InputText], botones [PrimaryButton], etc.).
 *
 * Proporciona una apariencia consistente con bordes redondeados, una ligera elevación
 * (sombra) y un color de fondo personalizable (por defecto un azul pálido).
 * El contenido del formulario se pasa a través de un slot composable (`content`)
 * que tiene un [ColumnScope], facilitando la disposición vertical de los elementos
 * con espaciado automático.
 *
 * @see Card Componente base de Material 3 utilizado como contenedor.
 * @see ColumnScope El scope proporcionado al slot `content` para organizar elementos verticalmente.
 * @see PaddingValues Tipo utilizado para definir el padding interno.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button // Para preview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme // Para preview
import androidx.compose.material3.Text // Para preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview // Importar Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * @brief Un [Composable] que actúa como contenedor estilizado para agrupar elementos de formulario.
 *
 * @details Renderiza un [Card] de Material 3 con esquinas redondeadas, elevación y un color
 * de fondo personalizable. Dentro de la tarjeta, organiza el contenido proporcionado
 * en una [Column] con espaciado vertical y alineación horizontal central por defecto.
 * Es ideal para encerrar secciones de un formulario o la totalidad de formularios cortos.
 *
 * @param modifier Un [Modifier] opcional que se aplica al [Card] contenedor principal.
 * Por defecto, ocupa el ancho máximo (`afillMaxWidth`).
 * @param backgroundColor El [Color] de fondo para la [Card]. Por defecto es un azul pálido (`0xFFC8DEFA`).
 * @param cornerRadiusDp El radio de las esquinas redondeadas de la [Card] en Dp. Por defecto `16`.
 * @param tonalElevation La elevación tonal ([Dp]) de la [Card], que controla la intensidad de la sombra.
 * Por defecto `3.dp`.
 * @param contentPadding El [PaddingValues] que se aplica dentro de la [Card], alrededor de la [Column]
 * que contiene el `content`. Por defecto `16.dp` en todos los lados.
 * @param content Un slot [Composable] lambda con receptor [ColumnScope]. Aquí es donde se deben
 * colocar los elementos del formulario (e.g., `InputText`, `Button`). Los elementos
 * se dispondrán verticalmente con un espaciado de `12.dp` entre ellos y se centrarán
 * horizontalmente por defecto.
 */
@Composable
fun FormContainer(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFC8DEFA),    // Azul suave por defecto
    cornerRadiusDp: Int = 16, // Radio de esquina por defecto
    tonalElevation: Dp = 3.dp, // Elevación por defecto
    contentPadding: PaddingValues = PaddingValues(16.dp), // Padding interno por defecto
    // Slot de contenido con ColumnScope
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(), // Ocupa el ancho completo por defecto
        shape = RoundedCornerShape(cornerRadiusDp.dp), // Aplica bordes redondeados
        // Configura la elevación (sombra)
        elevation = CardDefaults.cardElevation(defaultElevation = tonalElevation),
        // Configura el color de fondo del contenedor
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        // Columna interna para organizar el contenido verticalmente
        Column(
            // Aplica el padding interno alrededor del contenido
            modifier = Modifier.padding(contentPadding),
            // Añade espacio vertical entre cada elemento hijo de la columna
            verticalArrangement = Arrangement.spacedBy(12.dp),
            // Centra los elementos horizontalmente dentro de la columna
            horizontalAlignment = Alignment.CenterHorizontally,
            // Ejecuta la lambda de contenido proporcionada dentro del scope de esta columna
            content = content
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [FormContainer] con contenido de ejemplo y valores por defecto.
 */
@Preview(showBackground = true, name = "FormContainer Default")
@Composable
private fun FormContainerPreview() {
    MaterialTheme { // Necesario para estilos de texto y botón
        FormContainer(
            modifier = Modifier.padding(16.dp), // Padding externo para la preview
            // Usa colores, radio, elevación y padding por defecto
        ) {
            // Contenido de ejemplo
            Text("Inicio de Sesión", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp)) // Espacio adicional
            // Asumiendo InputText existe:
            // InputText(value = "", onValueChange = {}, placeholder = "Usuario")
            // InputPassword(value = "", onValueChange = {}, placeholder = "Contraseña")
            Text("Placeholder para Input Usuario") // Placeholder si no existe InputText
            Text("Placeholder para Input Contraseña") // Placeholder si no existe InputPassword
            Spacer(Modifier.height(8.dp))
            // Asumiendo PrimaryButton existe:
            // PrimaryButton(text = "Entrar", onClick = {}, modifier = Modifier.fillMaxWidth(0.6f))
            Button(onClick = {}) { Text("Entrar") } // Botón simple como placeholder
        }
    }
}

/**
 * @brief Previsualización del [FormContainer] con parámetros personalizados.
 */
@Preview(showBackground = true, name = "FormContainer Custom")
@Composable
private fun FormContainerCustomPreview() {
    MaterialTheme {
        FormContainer(
            modifier = Modifier.padding(16.dp),
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant, // Color de fondo diferente
            cornerRadiusDp = 8, // Esquinas menos redondeadas
            tonalElevation = 1.dp, // Menos sombra
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp) // Padding diferente
        ) {
            Text("Registro", style = MaterialTheme.typography.titleMedium)
            Text("Introduce tus datos para continuar.")
            Button(onClick = {}) { Text("Registrar") }
        }
    }
}
