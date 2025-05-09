/**
 * @file    HabitList.kt
 * @ingroup ui_component
 * @brief   Define Composables para mostrar la lista de hábitos del usuario y su estado vacío.
 *
 * @details Este archivo contiene los Composables [HabitList], que es el componente principal
 * para renderizar la lista de hábitos agrupados por categoría, y [EmptyState], que se
 * muestra cuando no existen hábitos. Utiliza componentes reutilizables como [HeaderBox],
 * [Category] (privado), [SettingItem], [RoundedIconButton], [Title], [Subtitle],
 * [Description] e [Icon] para construir la interfaz.
 *
 * La lista se puede desplazar verticalmente y el componente [HabitList] se encarga
 * de organizar los hábitos pasados en secciones según su categoría (actualmente filtrando
 * por nombre de icono como proxy de categoría). Proporciona callbacks para las acciones
 * de marcar/desmarcar, editar y añadir hábitos.
 *
 * @see com.app.tibibalance.ui.screens.habits.HabitUi Data class que representa un hábito en la UI.
 * @see com.app.tibibalance.ui.screens.habits.ShowHabitsScreen Pantalla que probablemente utiliza estos componentes.
 * @see SettingItem Componente utilizado para renderizar cada fila de hábito.
 * @see RoundedIconButton Botón utilizado para la acción de añadir.
 * @see Subtitle Usado para los títulos de categoría.
 * @see Title Usado en el HeaderBox.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.* // Import genérico para iconos usados
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text // Import explícito aunque Description/Title/Subtitle lo usen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Para las Previews
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.screens.habits.HabitUi

/**
 * @brief Renderiza la lista principal de hábitos, agrupados por categoría, o el estado vacío si no hay hábitos.
 *
 * @details Organiza los hábitos proporcionados en secciones lógicas utilizando el componente
 * privado [Category]. Incluye un encabezado ([HeaderBox]) y un botón flotante de acción
 * ([RoundedIconButton]) para añadir nuevos hábitos. El contenido es desplazable verticalmente.
 * La agrupación actual se basa en el nombre del icono asociado al `HabitUi`, que actúa como
 * proxy para la categoría (Salud, Productividad, Bienestar).
 *
 * @param habits La lista de objetos [HabitUi] que representan los hábitos a mostrar.
 * @param onCheck Callback invocado cuando el estado del [Checkbox] de un hábito cambia.
 * Recibe el [HabitUi] afectado y el nuevo estado `Boolean` (marcado/desmarcado).
 * @param onEdit Callback invocado cuando el usuario pulsa sobre la fila de un hábito
 * (excluyendo el checkbox). Recibe el [HabitUi] correspondiente.
 * @param onAdd Callback invocado cuando el usuario pulsa el botón de añadir hábito ([RoundedIconButton]).
 */
@Composable
internal fun HabitList(
    habits : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit,
    onAdd  : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el espacio disponible
            .verticalScroll(rememberScrollState()) // Permite desplazamiento vertical
            .padding(16.dp), // Padding general
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre elementos principales
    ) {
        // Encabezado de la sección
        HeaderBox("Mis hábitos")

        // Sección de categoría: Salud (filtrado por iconos específicos)
        Category(
            title = "Salud",
            rows = habits.filter { it.icon == "LocalDrink" || it.icon == "Bedtime" }, // Filtra hábitos de salud
            onCheck = onCheck, // Pasa los callbacks
            onEdit = onEdit
        )
        // Sección de categoría: Productividad (filtrado por icono)
        Category(
            title = "Productividad",
            rows = habits.filter { it.icon == "MenuBook" }, // Filtra hábitos de productividad
            onCheck = onCheck,
            onEdit = onEdit
        )
        // Sección de categoría: Bienestar (filtrado por icono)
        Category(
            title = "Bienestar",
            rows = habits.filter { it.icon == "SelfImprovement" }, // Filtra hábitos de bienestar
            onCheck = onCheck,
            onEdit = onEdit
        )

        // Botón flotante de acción (FAB simulado) para añadir hábitos, centrado al final
        Box(Modifier.fillMaxWidth(), Alignment.Center) {
            RoundedIconButton(
                onClick = onAdd,
                icon = Icons.Default.Add,
                contentDescription = "Agregar hábito",
                backgroundColor = Color(0xFF3EA8FE), // Color primario personalizado
                iconTint = Color.White
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
    title : String,
    rows  : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit
) {
    // No renderiza la sección si no hay hábitos en esta categoría
    if (rows.isEmpty()) return

    // Título de la categoría
    Subtitle(title)

    // Itera sobre los hábitos de esta categoría
    rows.forEach { h ->
        // Usa SettingItem para mostrar cada hábito
        SettingItem(
            // Icono principal (leading)
            leadingIcon = {
                Icon(
                    // Selecciona el icono basado en el nombre guardado en HabitUi
                    imageVector = Icons.Filled.run {
                        when (h.icon) {
                            "LocalDrink"       -> LocalDrink
                            "Bedtime"          -> Bedtime
                            "MenuBook"         -> Icons.AutoMirrored.Filled.MenuBook // Icono auto-reflejado
                            "SelfImprovement"  -> SelfImprovement
                            else               -> CheckBoxOutlineBlank // Icono por defecto si no coincide
                        }
                    },
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

// --- Previews (Opcionales, pero recomendadas) ---

/**
 * @brief Previsualización del componente [HabitList] con datos de ejemplo.
 */
@Preview(showBackground = true, name = "HabitList Preview")
@Composable
private fun HabitListPreview() {
    val sampleHabits = listOf(
        HabitUi("h1", "Beber 2L Agua", "LocalDrink", false),
        HabitUi("h2", "Dormir 8h", "Bedtime", true),
        HabitUi("h3", "Leer Libro", "MenuBook", false),
        HabitUi("h4", "Meditar", "SelfImprovement", false),
        HabitUi("h5", "Ejercicio Matutino", "FitnessCenter", true) // Icono no mapeado -> CheckBoxOutlineBlank
    )
    MaterialTheme {
        HabitList(
            habits = sampleHabits,
            onCheck = { _, _ -> },
            onEdit = { },
            onAdd = { }
        )
    }
}

/**
 * @brief Previsualización del componente [EmptyState].
 */
@Preview(showBackground = true, name = "EmptyState Preview")
@Composable
private fun EmptyStatePreview() {
    MaterialTheme {
        EmptyState(onAdd = {})
    }
}

/**
 * @brief Previsualización del componente [HeaderBox].
 */
@Preview(showBackground = true, name = "HeaderBox Preview", widthDp = 300)
@Composable
private fun HeaderBoxPreview() {
    MaterialTheme {
        HeaderBox("Título de Sección")
    }
}