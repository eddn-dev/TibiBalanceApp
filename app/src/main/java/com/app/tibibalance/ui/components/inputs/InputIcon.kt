/*
 * ui/components/inputs/InputIcon.kt
 *
 * Componente selector de icono basado en la librería Material 3.
 */
package com.app.tibibalance.ui.components.inputs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items      // ⬅️ importa la sobre-carga correcta
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/* ---------- 1 · listado de nombres ---------- */
val IconNames = listOf(
    // existentes
    "LocalDrink", "Book", "Bedtime", "WbSunny", "SelfImprovement",
    "FitnessCenter", "DirectionsRun", "PedalBike", "SportsGymnastics", "Walk",
    "Restaurant", "Fastfood", "SmokeFree", "Spa",
    "Brush", "MusicNote", "Piano",
    "Code", "School",

    // nuevos (28)
    "Alarm", "AlarmOn", "AlarmOff", "AddShoppingCart", "CalendarToday",
    "Camera", "CheckCircle", "Delete", "Edit", "Email",
    "Favorite", "FavoriteBorder", "Home", "Language", "LightMode",
    "Lock", "Mood", "Notifications", "Phone", "PlayArrow",
    "Print", "Save", "Search", "Settings", "Share",
    "ShoppingCart", "Star", "ThumbUp"
)

/* ---------- 2 · mapeo nombre → ImageVector ---------- */
fun iconByName(name: String): ImageVector = when (name) {
    // existentes
    "LocalDrink"       -> Icons.Default.LocalDrink
    "Book"             -> Icons.Default.Book
    "Bedtime"          -> Icons.Default.Bedtime
    "WbSunny"          -> Icons.Default.WbSunny
    "SelfImprovement"  -> Icons.Default.SelfImprovement
    "FitnessCenter"    -> Icons.Default.FitnessCenter
    "DirectionsRun"    -> Icons.AutoMirrored.Filled.DirectionsRun
    "PedalBike"        -> Icons.Default.PedalBike
    "SportsGymnastics" -> Icons.Default.SportsGymnastics
    "Walk"             -> Icons.AutoMirrored.Filled.DirectionsWalk
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
    "Camera"           -> Icons.Default.Camera      // usa la cámara básica
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

    else               -> Icons.AutoMirrored.Filled.Help // fallback genérico
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputIcon(
    iconName    : String,
    onChange    : (String) -> Unit,
    modifier    : Modifier = Modifier,
    description : String?  = null,
    isEditing   : Boolean  = true
) {
    /* ---- estado interno ---- */
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet  by remember { mutableStateOf(false) }
    val scope      = rememberCoroutineScope()

    /* ---- contenedor con posible lápiz ---- */
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box {
            FilledTonalIconButton(
                onClick  = { if (isEditing) showSheet = true },
                modifier = Modifier.size(72.dp),
                enabled  = isEditing
            ) {
                Icon(
                    painter = rememberVectorPainter(iconByName(iconName)),
                    contentDescription = "Icono seleccionado"
                )
            }
            if (isEditing) {
                Icon(                           // lápiz super-puesto
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Cambiar icono",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .offset(4.dp, 4.dp)
                        .clip(CircleShape)
                )
            }
        }
        description?.let {
            Spacer(Modifier.height(4.dp))
            Text(it, style = MaterialTheme.typography.bodySmall)
        }
    }

    /* ---- hoja inferior con la parrilla de iconos ---- */
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState       = sheetState
        ) {
            LazyVerticalGrid(
                columns  = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxHeight(0.70f)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp)
            ) {
                items(IconNames) { name ->
                    IconToggleButton(
                        checked = name == iconName,
                        onCheckedChange = {
                            onChange(name)      // avisa al formulario / VM
                            scope.launch { sheetState.hide() }
                                .invokeOnCompletion { showSheet = false }
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            painter = rememberVectorPainter(iconByName(name)),
                            contentDescription = name
                        )
                    }
                }
            }
        }
    }
}