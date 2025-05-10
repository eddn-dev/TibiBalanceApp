/**
 * @file SuggestionStep.kt
 * @ingroup ui_wizard_step
 * @brief Define el Composable para el paso inicial del asistente de creación de hábitos, mostrando plantillas sugeridas.
 *
 * @details
 * Este Composable presenta al usuario una lista de hábitos predefinidos ([HabitTemplate]),
 * agrupados por categoría ([HabitCategory]). Es típicamente el primer paso que ve
 * el usuario al iniciar el flujo de creación de un nuevo hábito desde cero.
 *
 * Cada plantilla se muestra en una fila ([SuggestionRow], que internamente usa [SettingItem])
 * con su icono y nombre. Un botón de añadir (+) permite al usuario seleccionar una
 * plantilla como punto de partida para su nuevo hábito. Al seleccionar una plantilla,
 * se invoca el callback `onSuggestion`.
 *
 * La lista de plantillas (`templates`) se recibe como parámetro, agrupándose dinámicamente
 * por categoría para la renderización. Utiliza componentes reutilizables como [Title],
 * [Subtitle], y [SuggestionRow] para construir la interfaz.
 *
 * @see com.app.tibibalance.ui.wizard.AddHabitViewModel ViewModel que probablemente gestiona el estado y la lógica de este asistente.
 * @see com.app.tibibalance.domain.model.HabitTemplate Modelo de dominio para las plantillas de hábitos.
 * @see com.app.tibibalance.domain.model.HabitCategory Enum para las categorías de hábitos.
 * @see SuggestionRow Composable privado para renderizar cada fila de sugerencia.
 * @see SettingItem Componente base reutilizado por SuggestionRow.
 * @see Title, Subtitle Componentes de texto reutilizables.
 * @see RoundedIconButton Componente para el botón de añadir.
 * @see com.app.tibibalance.ui.components.inputs.iconByName Helper para obtener el ImageVector del icono.
 */
/* ui/wizard/step/SuggestionStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.ui.components.* // Importa componentes generales como SettingItem, RoundedIconButton
import com.app.tibibalance.ui.components.inputs.iconByName // Helper para mapear nombre a ImageVector
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

/**
 * @brief Composable para la pantalla de sugerencias de hábitos en el asistente.
 *
 * @details Muestra una lista de [HabitTemplate] agrupadas por [HabitCategory].
 * Cada plantilla es seleccionable para iniciar la creación de un hábito basado en ella.
 *
 * @param templates La lista de [HabitTemplate] a mostrar como sugerencias.
 * @param onSuggestion Callback invocado cuando el usuario selecciona (pulsa el botón '+')
 * una plantilla. Recibe la [HabitTemplate] seleccionada.
 */
@Composable
fun SuggestionStep(
    templates   : List<HabitTemplate>,
    onSuggestion: (HabitTemplate) -> Unit
) {
    val scrollState = rememberScrollState()
    // Columna principal que contiene el título y la lista agrupada.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 20.dp) // Padding ajustado.
    ) {
        // Título de la pantalla.
        Title(
            text      = "Hábitos sugeridos",
            modifier  = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp), // Separación del título con la lista.
            textAlign = TextAlign.Center // Título centrado.
        )

        /* Agrupa las plantillas por su categoría */
        templates
            .groupBy { it.category } // Agrupa la lista por HabitCategory.
            // Itera sobre cada grupo (categoría y su lista de plantillas).
            .forEach { (categoryEnum, templateListInCategory) ->
                // Muestra el nombre de la categoría como subtítulo.
                Subtitle(
                    text     = categoryEnum.display, // Usa el nombre legible del Enum.
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp) // Padding vertical del subtítulo.
                )
                // Columna para listar las plantillas de esta categoría.
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp) // Espacio vertical entre filas de hábitos.
                ) {
                    // Itera sobre las plantillas de la categoría actual.
                    templateListInCategory.forEach { template ->
                        // Renderiza una fila para cada plantilla.
                        SuggestionRow(template) { onSuggestion(template) } // Pasa la plantilla y el callback.
                    }
                }
                Spacer(Modifier.height(8.dp)) // Espacio adicional entre categorías.
            }
    }
}

/**
 * @brief Composable privado que renderiza una fila individual para una plantilla de hábito sugerida.
 *
 * @details Utiliza [SettingItem] como base para mostrar el icono y el nombre de la plantilla.
 * Añade un [RoundedIconButton] con un icono '+' como elemento `trailing` para la acción de añadir.
 * Tanto el botón '+' como la fila completa son clicables y ejecutan el callback `onAdd`.
 *
 * @param tpl La [HabitTemplate] cuyos datos se van a mostrar.
 * @param onAdd La función lambda que se invoca cuando se pulsa la fila o el botón '+'.
 */
@Composable
private fun SuggestionRow(
    tpl  : HabitTemplate,
    onAdd: () -> Unit
) = SettingItem( // Usa el componente reutilizable SettingItem.
    leadingIcon = { // Define el icono principal.
        Icon(
            // Obtiene el ImageVector a partir del nombre del icono en la plantilla.
            painter            = rememberVectorPainter(iconByName(tpl.icon)),
            contentDescription = tpl.name, // Descripción para accesibilidad.
            modifier           = Modifier.size(32.dp) // Tamaño del icono.
            // El tinte por defecto de Icon dentro de SettingItem debería funcionar.
        )
    },
    text = tpl.name, // Muestra el nombre de la plantilla.
    trailing = { // Define el elemento al final de la fila.
        RoundedIconButton(
            onClick            = onAdd, // Acción al pulsar el botón.
            icon               = Icons.Default.Add, // Icono de añadir.
            contentDescription = "Agregar hábito ${tpl.name}", // Descripción específica.
            modifier           = Modifier.size(32.dp), // Tamaño más pequeño para el botón de añadir.
            backgroundColor    = Color(0xFF3EA8FE), // Color de fondo específico.
            iconTint           = Color.White // Tinte blanco para el icono '+'
        )
    },
    onClick = onAdd // Hace que toda la fila sea clicable y ejecute la misma acción.
)