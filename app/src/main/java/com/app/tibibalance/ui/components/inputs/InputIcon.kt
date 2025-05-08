/**
 * @file    InputIcon.kt
 * @ingroup ui_component_input // Grupo específico para componentes de entrada
 * @brief   Define un [Composable] para seleccionar un icono de Material Design.
 *
 * @details Este archivo contiene el componente [InputIcon], que permite al usuario
 * visualizar un icono actualmente seleccionado y, si está en modo de edición,
 * abrir una hoja modal inferior ([ModalBottomSheet]) para elegir un nuevo icono
 * de una lista predefinida ([IconNames]).
 *
 * También incluye la lista [IconNames] y la función helper [iconByName] para
 * mapear los nombres de los iconos a sus respectivos [ImageVector] de Material Icons.
 *
 * @see InputIcon El componente principal.
 * @see IconNames Lista de nombres de iconos disponibles.
 * @see iconByName Función para obtener el ImageVector a partir de un nombre.
 * @see ModalBottomSheet Componente de Material 3 para la hoja modal.
 * @see LazyVerticalGrid Componente para mostrar la cuadrícula de iconos.
 * @see IconToggleButton Botón utilizado para cada icono seleccionable en la cuadrícula.
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items // Importa la sobrecarga correcta para List
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview // Importar para Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/* ---------- 1. Lista de nombres de iconos disponibles ---------- */

/**
 * @brief Lista predefinida de nombres de iconos de Material Design disponibles para seleccionar.
 * @details Contiene una selección de iconos estándar y auto-reflejados (`AutoMirrored`)
 * relevantes para la aplicación. Esta lista se utiliza para poblar la cuadrícula
 * en la hoja modal de selección.
 * @see iconByName Función que mapea estos nombres a [ImageVector].
 */
val IconNames = listOf(
    // Iconos existentes previamente
    "LocalDrink", "Book", "Bedtime", "WbSunny", "SelfImprovement",
    "FitnessCenter", "DirectionsRun", "PedalBike", "SportsGymnastics", "Walk",
    "Restaurant", "Fastfood", "SmokeFree", "Spa",
    "Brush", "MusicNote", "Piano",
    "Code", "School",

    // Nuevos iconos añadidos (Total = 19 existentes + 28 nuevos = 47 iconos)
    "Alarm", "AlarmOn", "AlarmOff", "AddShoppingCart", "CalendarToday",
    "Camera", "CheckCircle", "Delete", "Edit", "Email",
    "Favorite", "FavoriteBorder", "Home", "Language", "LightMode",
    "Lock", "Mood", "Notifications", "Phone", "PlayArrow",
    "Print", "Save", "Search", "Settings", "Share",
    "ShoppingCart", "Star", "ThumbUp"
)

/* ---------- 2. Función de mapeo Nombre -> ImageVector ---------- */

/**
 * @brief Devuelve el [ImageVector] de Material Icons correspondiente al nombre proporcionado.
 * @details Realiza un mapeo explícito desde un nombre de icono [String] (de la lista [IconNames])
 * a su objeto [ImageVector] real (e.g., `Icons.Default.LocalDrink`).
 * Incluye iconos estándar (`Icons.Default.*`) y auto-reflejados (`Icons.AutoMirrored.Filled.*`)
 * para soportar correctamente la dirección de lectura (LTR/RTL).
 * Si el nombre no se encuentra en el mapeo, devuelve un icono de ayuda genérico como fallback.
 *
 * @param name El nombre [String] del icono a buscar (debería ser uno de [IconNames]).
 * @return El [ImageVector] correspondiente, o [Icons.AutoMirrored.Filled.Help] si no se encuentra.
 */
fun iconByName(name: String): ImageVector = when (name) {
    // existentes
    "LocalDrink"       -> Icons.Default.LocalDrink
    "Book"             -> Icons.Default.Book
    "Bedtime"          -> Icons.Default.Bedtime
    "WbSunny"          -> Icons.Default.WbSunny
    "SelfImprovement"  -> Icons.Default.SelfImprovement
    "FitnessCenter"    -> Icons.Default.FitnessCenter
    "DirectionsRun"    -> Icons.AutoMirrored.Filled.DirectionsRun // AutoMirrored
    "PedalBike"        -> Icons.Default.PedalBike
    "SportsGymnastics" -> Icons.Default.SportsGymnastics
    "Walk"             -> Icons.AutoMirrored.Filled.DirectionsWalk // AutoMirrored
    "Restaurant"       -> Icons.Default.Restaurant
    "Fastfood"         -> Icons.Default.Fastfood
    "SmokeFree"        -> Icons.Default.SmokeFree
    "Spa"              -> Icons.Default.Spa
    "Brush"            -> Icons.Default.Brush
    "MusicNote"        -> Icons.Default.MusicNote
    "Piano"            -> Icons.Default.Piano
    "Code"             -> Icons.Default.Code
    "School"           -> Icons.Default.School

    // nuevos
    "Alarm"            -> Icons.Default.Alarm
    "AlarmOn"          -> Icons.Default.AlarmOn
    "AlarmOff"         -> Icons.Default.AlarmOff
    "AddShoppingCart"  -> Icons.Default.AddShoppingCart
    "CalendarToday"    -> Icons.Default.CalendarToday
    "Camera"           -> Icons.Default.Camera
    "CheckCircle"      -> Icons.Default.CheckCircle
    "Delete"           -> Icons.Default.Delete
    "Edit"             -> Icons.Default.Edit
    "Email"            -> Icons.Default.Email
    "Favorite"         -> Icons.Default.Favorite
    "FavoriteBorder"   -> Icons.Default.FavoriteBorder
    "Home"             -> Icons.Default.Home
    "Language"         -> Icons.Default.Language
    "LightMode"        -> Icons.Default.LightMode
    "Lock"             -> Icons.Default.Lock
    "Mood"             -> Icons.Default.Mood
    "Notifications"    -> Icons.Default.Notifications
    "Phone"            -> Icons.Default.Phone
    "PlayArrow"        -> Icons.Default.PlayArrow
    "Print"            -> Icons.Default.Print
    "Save"             -> Icons.Default.Save
    "Search"           -> Icons.Default.Search
    "Settings"         -> Icons.Default.Settings
    "Share"            -> Icons.Default.Share
    "ShoppingCart"     -> Icons.Default.ShoppingCart
    "Star"             -> Icons.Default.Star
    "ThumbUp"          -> Icons.Default.ThumbUp

    // Fallback si el nombre no coincide
    else               -> Icons.AutoMirrored.Filled.Help
}

/* ---------- 3. Composable Principal ---------- */

/**
 * @brief Un [Composable] que muestra un icono seleccionable y abre una hoja modal para elegir uno nuevo.
 *
 * @details Este componente presenta un botón ([FilledTonalIconButton]) que muestra el icono
 * actualmente seleccionado (`iconName`). Si `isEditing` es `true`, al pulsar este botón
 * se abre una [ModalBottomSheet] que contiene una [LazyVerticalGrid] con todos los iconos
 * disponibles en [IconNames].
 *
 * El usuario puede seleccionar un nuevo icono pulsando sobre él en la cuadrícula. Al hacerlo,
 * se llama al callback `onChange` con el nuevo nombre del icono, y la hoja modal se cierra
 * automáticamente. Si `isEditing` es `true`, también se muestra un pequeño icono de lápiz
 * superpuesto en la esquina del botón principal. Opcionalmente, se puede mostrar un texto
 * descriptivo debajo del botón.
 *
 * @param iconName El nombre [String] del icono actualmente seleccionado. Debe ser uno de los
 * nombres definidos en [IconNames].
 * @param onChange La función lambda que se invoca cuando el usuario selecciona un nuevo icono
 * de la hoja modal. Recibe el nombre [String] del nuevo icono como parámetro.
 * @param modifier Un [Modifier] opcional para aplicar al [Column] contenedor principal,
 * permitiendo ajustar el padding, alineación, etc.
 * @param description Un [String] opcional que se muestra como texto debajo del botón del icono.
 * Si es `null`, no se muestra nada.
 * @param isEditing Un [Boolean] que controla si el componente está en modo de edición.
 * Si es `true`, el botón es interactivo, se muestra el lápiz de edición y se puede
 * abrir la hoja modal. Si es `false`, el botón se deshabilita y el lápiz se oculta.
 * Por defecto `true`.
 */
@OptIn(ExperimentalMaterial3Api::class) // Necesario para ModalBottomSheetState y ModalBottomSheet
@Composable
fun InputIcon(
    iconName    : String,
    onChange    : (String) -> Unit,
    modifier    : Modifier = Modifier,
    description : String?  = null, // Descripción opcional
    isEditing   : Boolean  = true // Editable por defecto
) {
    // Estado para controlar la hoja modal inferior (abierta/cerrada)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Estado para controlar si la hoja debe mostrarse o no
    var showSheet  by remember { mutableStateOf(false) }
    // Scope para lanzar corrutinas (para cerrar la hoja animadamente)
    val scope      = rememberCoroutineScope()

    // Columna principal que contiene el botón y la descripción opcional
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Centra el contenido
        modifier = modifier // Aplica modificador externo
    ) {
        // Box para permitir superponer el icono de edición
        Box {
            // Botón tonal que muestra el icono actual y abre la hoja
            FilledTonalIconButton(
                onClick  = { if (isEditing) showSheet = true }, // Abre la hoja si está editando
                modifier = Modifier.size(72.dp), // Tamaño del botón
                enabled  = isEditing // Habilita/deshabilita el botón
            ) {
                // Icono actual
                Icon(
                    painter = rememberVectorPainter(image = iconByName(iconName)), // Obtiene el ImageVector
                    contentDescription = "Icono seleccionado: $iconName" // Descripción para accesibilidad
                )
            }
            // Icono de lápiz (solo si está editando)
            if (isEditing) {
                Icon(
                    imageVector = Icons.Default.Edit, // Icono de editar
                    contentDescription = "Cambiar icono", // Descripción
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Posición en esquina inferior derecha
                        .size(24.dp) // Tamaño del lápiz
                        .offset(x = 4.dp, y = 4.dp) // Desplazamiento ligero
                        .clip(CircleShape), // Forma circular
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Color del lápiz
                )
            }
        }
        // Muestra la descripción si se proporcionó
        description?.let { descText ->
            Spacer(Modifier.height(4.dp)) // Espacio
            Text(descText, style = MaterialTheme.typography.bodySmall) // Texto descriptivo
        }
    }

    // Hoja modal inferior que se muestra condicionalmente
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }, // Cierra la hoja si se pulsa fuera o se desliza hacia abajo
            sheetState       = sheetState // Estado que controla la animación y visibilidad
        ) {
            // Cuadrícula perezosa para mostrar los iconos seleccionables
            LazyVerticalGrid(
                columns  = GridCells.Fixed(5), // 5 columnas fijas
                modifier = Modifier
                    .fillMaxWidth() // Ocupa el ancho
                    .fillMaxHeight(0.6f) // Altura máxima del 60% de la pantalla
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Padding interno
                horizontalArrangement = Arrangement.spacedBy(12.dp), // Espacio horizontal entre iconos
                verticalArrangement   = Arrangement.spacedBy(12.dp) // Espacio vertical entre iconos
            ) {
                // Itera sobre la lista de nombres de iconos
                items(items = IconNames, key = { it }) { name -> // Usa el nombre como key para eficiencia
                    // Botón de tipo Toggle para cada icono
                    IconToggleButton(
                        checked = name == iconName, // Marcado si es el icono actual
                        onCheckedChange = { isChecked ->
                            // Se llama cuando el estado de 'checked' cambia (al pulsar)
                            if (isChecked) { // Solo actúa si se está marcando
                                onChange(name) // Llama al callback con el nuevo nombre
                                // Cierra la hoja modal de forma asíncrona
                                scope.launch {
                                    sheetState.hide() // Inicia la animación de ocultar
                                }.invokeOnCompletion { // Se ejecuta cuando la corrutina (y la animación) terminan
                                    // Asegura que el estado se actualice solo si la hoja realmente se ocultó
                                    if (!sheetState.isVisible) {
                                        showSheet = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp), // Tamaño de cada celda/botón de icono
                        colors = IconButtonDefaults.iconToggleButtonColors(
                            // Colores cuando el botón está seleccionado (checked)
                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            // Los colores no seleccionados usan los defaults del tema
                        )
                    ) {
                        // El icono a mostrar dentro del botón
                        Icon(
                            painter = rememberVectorPainter(image = iconByName(name)), // Obtiene el ImageVector
                            contentDescription = name // Descripción (nombre del icono)
                        )
                    }
                }
            }
            // Espacio al final de la hoja para que el último renglón no quede pegado abajo
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [InputIcon] en modo editable.
 */
@Preview(showBackground = true, name = "InputIcon Editable")
@Composable
private fun InputIconEditablePreview() {
    // Simula el estado del icono seleccionado
    var selectedIcon by remember { mutableStateOf("FitnessCenter") }
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) { // Añade padding para ver mejor
            InputIcon(
                iconName = selectedIcon,
                onChange = { selectedIcon = it }, // Actualiza el estado simulado
                description = "Selecciona un icono",
                isEditing = true // Modo editable
            )
        }
    }
}

/**
 * @brief Previsualización del [InputIcon] en modo no editable (solo visualización).
 */
@Preview(showBackground = true, name = "InputIcon Not Editable")
@Composable
private fun InputIconNotEditablePreview() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InputIcon(
                iconName = "LocalDrink", // Muestra un icono específico
                onChange = { }, // La acción no importa si no es editable
                description = "Icono (no editable)",
                isEditing = false // Modo no editable
            )
        }
    }
}
