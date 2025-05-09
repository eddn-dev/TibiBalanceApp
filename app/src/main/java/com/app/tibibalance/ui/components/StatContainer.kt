/**
 * @file    StatContainer.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o específicos de visualización de datos
 * @brief   Define un [Composable] reutilizable para mostrar una estadística simple con un icono, valor y etiqueta.
 *
 * @details Este archivo contiene el [Composable] `StatContainer`. Este componente
 * renderiza un contenedor con fondo de color claro y bordes redondeados.
 * Dentro del contenedor, muestra opcionalmente un icono a la izquierda y, a su derecha,
 * una columna con un valor numérico o textual ([Text] con estilo `headlineSmall`, `Bold`)
 * y debajo una etiqueta descriptiva ([Text] con estilo `bodyMedium`, `DarkGray`).
 *
 * Está diseñado para presentar métricas o estadísticas clave de forma clara y
 * visualmente agrupada, comúnmente utilizado en dashboards o secciones de resumen.
 *
 * @see androidx.compose.foundation.layout.Row Layout principal para alinear icono y textos.
 * @see androidx.compose.foundation.layout.Column Layout para apilar valor y etiqueta.
 * @see androidx.compose.material3.Text Usado para mostrar el valor y la etiqueta.
 * @see androidx.compose.material3.Icon Usado para mostrar el icono opcional.
 * @see androidx.compose.ui.text.font.FontWeight Usado para aplicar negrita al valor.
 * @see androidx.compose.material3.MaterialTheme Usado para obtener los estilos de texto base.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons // Para Preview
import androidx.compose.material.icons.filled.DirectionsRun // Para Preview
import androidx.compose.material.icons.filled.Favorite // Para Preview
import androidx.compose.material3.Icon // Para el nuevo parámetro 'icon'
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Para el tipo del icono
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que muestra una estadística (con icono opcional, valor y etiqueta) dentro de un contenedor estilizado.
 *
 * @details Renderiza un [Row] principal. A la izquierda, puede mostrar un [Icon] opcional.
 * A la derecha del icono (o al inicio si no hay icono), una [Column] centrada horizontalmente
 * muestra el `value` con un estilo de texto prominente y el `label` debajo.
 * Todo el conjunto tiene un fondo (`backgroundColor`) y esquinas redondeadas (`cornerRadius`).
 *
 * @param value El [String] que representa el valor numérico o textual de la estadística
 * (e.g., "5,230", "78 bpm").
 * @param label El [String] que describe qué representa el valor (e.g., "Pasos", "Frecuencia Cardíaca").
 * @param icon Un [ImageVector] opcional que se mostrará a la izquierda de los textos. Si es `null`, no se muestra ningún icono.
 * @param iconTint El [Color] para el tinte del `icon`. Por defecto es `Color.Black`.
 * @param backgroundColor El [Color] de fondo para el contenedor. Por defecto es un cian muy claro (`0xFFE0F7FA`).
 * @param cornerRadius El radio ([Dp]) para las esquinas redondeadas del contenedor. Por defecto `12.dp`.
 * @param modifier Un [Modifier] opcional que se aplica al contenedor principal.
 */
@Composable
fun StatContainer(
    value: String,
    label: String,
    icon: ImageVector? = null, // Nuevo parámetro para el icono
    iconTint: Color = Color.Black, // Tinte por defecto para el icono
    backgroundColor: Color = Color(0xFFE0F7FA),
    cornerRadius: Dp = 12.dp,
    modifier: Modifier = Modifier
) {
    // Contenedor principal con fondo y forma
    Box(
        modifier = modifier
            .background(backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .padding(vertical = 16.dp, horizontal = 16.dp) // Ajustar padding si es necesario con icono
    ) {
        // Row para alinear icono (si existe) y la columna de textos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre icono y textos
        ) {
            // Muestra el icono si se proporciona
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = label, // Usa la etiqueta como descripción del icono para accesibilidad
                    tint = iconTint,
                    modifier = Modifier.size(32.dp) // Tamaño del icono, ajustable según diseño
                )
            }

            // Columna para el valor y la etiqueta
            Column(
                // Si no hay icono, esta columna podría necesitar alineación diferente o fillMaxWidth
                // dependiendo del diseño deseado. Aquí asume que el Row maneja el ancho.
                horizontalAlignment = Alignment.Start // Alinea textos a la izquierda si hay icono
                // o Alignment.CenterHorizontally si se prefiere centrado sin icono
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [StatContainer] con icono, valor y etiqueta.
 */
@Preview(showBackground = true, name = "StatContainer With Icon")
@Composable
private fun StatContainerWithIconPreview() {
    MaterialTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            StatContainer(
                icon = Icons.Filled.DirectionsRun,
                value = "5,230",
                label = "Pasos hoy",
                modifier = Modifier.weight(1f)
            )
            StatContainer(
                icon = Icons.Filled.Favorite,
                iconTint = Color.Red,
                value = "78 bpm",
                label = "Ritmo Cardíaco",
                backgroundColor = Color(0xFFFFF9E0),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * @brief Previsualización del [StatContainer] sin icono (comportamiento original).
 */
@Preview(showBackground = true, name = "StatContainer No Icon")
@Composable
private fun StatContainerNoIconPreview() {
    MaterialTheme {
        StatContainer(
            value = "1200",
            label = "Calorías",
            modifier = Modifier.padding(16.dp)
            // icon = null implícito
        )
    }
}