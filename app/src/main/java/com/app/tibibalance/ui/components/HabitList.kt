/**
 * @file    HabitList.kt
 * @ingroup ui_component
 * @brief   Composables para mostrar la lista de hábitos agrupados dinámicamente.
 *
 * @details Se reemplazó la lógica de filtros rígidos por una agrupación
 * mediante `groupBy { it.category }`.  Si `HabitUi` aún no expone
 * `category`, agrega la propiedad o calcula una heurística en el mapper.
 *
 * Las secciones se pintan en el orden dado por [sectionOrder]; cualquier
 * categoría no listada caerá en "Otros".
 *
 * @see HabitUi Modelo de UI.  Debe contener `val category: String`.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.domain.model.HabitCategory
import com.app.tibibalance.ui.components.inputs.iconByName
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.screens.habits.HabitUi

/* ────────────────────────────────────────────────────────────────── */
/* Sección pública                                                   */
/* ────────────────────────────────────────────────────────────────── */

/**
 * @brief Renderiza la lista de hábitos agrupados de forma dinámica.
 *
 * @param habits  Lista de [HabitUi] a mostrar.
 * @param onCheck Callback del checkbox.
 * @param onEdit  Callback al pulsar la fila.
 * @param onAdd   Callback del botón “+”.
 */
@Composable
internal fun HabitList(
    habits : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit,
    onAdd  : () -> Unit
) {
    /** Orden de secciones preferido; lo que no esté aquí va a “Otros”. */
    val sectionOrder = listOf(
        HabitCategory.SALUD,
        HabitCategory.PRODUCTIVIDAD,
        HabitCategory.BIENESTAR,
        HabitCategory.PERSONALIZADA
    )



    /* Agrupa por categoría (clave String) */
    val grouped: Map<String, List<HabitUi>> =
        habits.groupBy { it.category.ifBlank { "Otros" } }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderBox("Mis hábitos")

        sectionOrder.forEach { cat ->
            Category(
                title   = cat,                 // enum directo
                rows    = grouped[cat.display].orEmpty(),
                onCheck = onCheck,
                onEdit  = onEdit
            )
        }

        /* FAB centrado */
        Box(Modifier.fillMaxWidth(), Alignment.Center) {
            RoundedIconButton(
                onClick            = onAdd,
                icon               = Icons.Default.Add,
                contentDescription = "Agregar hábito",
                backgroundColor    = Color(0xFF3EA8FE),
                iconTint           = Color.White
            )
        }
    }
}



/**
 * @brief Composable privado para renderizar una sección de categoría dentro de [HabitList].
 *
 * @details Muestra un [Subtitle] con el nombre de la categoría y luego itera sobre la lista
 * `rows` para mostrar cada hábito usando el componente [SettingItem]. Si la lista `rows`
 * está vacía, el componente no renderiza nada.
 *
 * @param title El título de la categoría (e.g., "Salud").
 * @param rows La lista de [HabitUi] pertenecientes a esta categoría.
 * @param onCheck Callback para el cambio de estado del Checkbox, pasado a cada [SettingItem].
 * @param onEdit Callback para la acción de clic en la fila, pasado a cada [SettingItem].
 */
@Composable
private fun Category(
    title   : HabitCategory,
    rows  : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit
) {
    // No renderiza la sección si no hay hábitos en esta categoría
    if (rows.isEmpty()) return

    // Título de la categoría
    Subtitle(title.display)

    // Itera sobre los hábitos de esta categoría
    rows.forEach { h ->
        // Usa SettingItem para mostrar cada hábito
        SettingItem(
            // Icono principal (leading)
            leadingIcon = {
                Icon(
                    // Selecciona el icono basado en el nombre guardado en HabitUi
                    imageVector = iconByName(h.icon),
                    contentDescription = null, // Icono decorativo dentro de un item con texto
                    tint = MaterialTheme.colorScheme.primary // Tinte primario del tema
                )
            },
            // Texto principal (nombre del hábito)
            text = h.name,
            // Elemento al final (trailing) - Checkbox
            trailing = {
                Checkbox(
                    checked = h.checked, // Estado actual del hábito
                    onCheckedChange = { isChecked -> onCheck(h, isChecked) } // Llama al callback al cambiar
                )
            },
            // Acción al hacer clic en toda la fila (excepto el checkbox)
            onClick = { onEdit(h) }
        )
    }
}

/**
 * @brief Muestra el estado visual cuando la lista de hábitos está vacía.
 *
 * @details Presenta un icono indicativo (flecha hacia abajo), un texto descriptivo
 * animando al usuario a crear su primer hábito, y un [RoundedIconButton] con el
 * icono de añadir (+) para iniciar la acción de creación.
 *
 * @param onAdd Callback que se invoca al pulsar el botón de añadir.
 */
@Composable
internal fun EmptyState(onAdd: () -> Unit) = Column(
    modifier = Modifier
        .fillMaxSize() // Ocupa todo el espacio disponible
        .padding(32.dp), // Padding generoso
    horizontalAlignment = Alignment.CenterHorizontally, // Centra contenido horizontalmente
    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically) // Espacio vertical y centrado vertical
) {
    // Icono decorativo que sugiere la acción a realizar
    Icon(
        imageVector       = Icons.Filled.ArrowDownward,
        contentDescription = null, // Icono decorativo, el texto explica la acción
        tint              = MaterialTheme.colorScheme.primary, // Usa color primario del tema
        modifier          = Modifier.size(96.dp) // Tamaño grande para el icono
    )

    // Texto explicativo
    Description(
        text = "¡Aún no tienes hábitos!\nPulsa el + para crear tu primer hábito.",
        textAlign = TextAlign.Center // Texto centrado
    )

    // Botón para añadir el primer hábito
    RoundedIconButton(
        onClick            = onAdd, // Callback para iniciar la creación
        icon               = Icons.Default.Add, // Icono de añadir
        contentDescription = "Agregar hábito", // Descripción para accesibilidad
        backgroundColor    = Color(0xFF3EA8FE), // Color primario personalizado
        iconTint           = Color.White
    )
}


/**
 * @brief Composable privado que crea una caja con fondo y bordes redondeados para un título.
 * @details Usado como encabezado simple para la lista de hábitos.
 *
 * @param text El texto a mostrar dentro del [Title].
 */
@Composable
private fun HeaderBox(text: String) = Box(
    modifier = Modifier
        .fillMaxWidth() // Ocupa todo el ancho
        // Fondo con color específico y esquinas redondeadas
        .background(Color(0xFF85C3DE), RoundedCornerShape(15.dp))
        .padding(vertical = 8.dp), // Padding vertical interno
    contentAlignment = Alignment.Center // Centra el contenido (el Title)
) {
    Title(text, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

/**
 * @brief Composable interno de utilidad para centrar texto en pantalla.
 * @details Usado principalmente para mostrar estados de carga o error de forma simple.
 *
 * @param txt El texto a mostrar.
 */
@Composable
internal fun Centered(txt: String) = Box(
    Modifier.fillMaxSize(), Alignment.Center
) { androidx.compose.material3.Text(txt, fontSize = 32.sp) }
