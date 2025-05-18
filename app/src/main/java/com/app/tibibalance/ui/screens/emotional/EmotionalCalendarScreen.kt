// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionalCalendarScreen.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.CalendarGrid
import com.app.tibibalance.ui.components.Centered
import com.app.tibibalance.ui.components.EmotionDay
import com.app.tibibalance.ui.components.TextButtonLink
import com.app.tibibalance.ui.components.containers.HabitContainer
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * @file    EmotionalCalendarScreen.kt
 * @ingroup ui_screens_emotional
 * @brief   Pantalla de calendario emocional con gestión de modales.
 *
 * @details
 * - Observa `vm.ui: StateFlow<EmotionalUiState>` via `collectAsState()`
 * - Maneja eventos one-shot con `LaunchedEffect { vm.events.collect { … } }`
 * - Usa un `Box` con fondo de gradiente (`Brush.verticalGradient`)
 * - Control de visibilidad de modales con `remember { mutableStateOf(...) }`
 */
@Composable
fun EmotionalCalendarScreen(
    vm: EmotionalCalendarViewModel = hiltViewModel()
) {
    // 1) Estado del ViewModel
    val uiState by vm.ui.collectAsState()  // collectAsState() convierte StateFlow en Compose State :contentReference[oaicite:11]{index=11}

    // 2) Estados locales para modales
    var showModalFor    by remember { mutableStateOf<LocalDate?>(null) }               // registro :contentReference[oaicite:12]{index=12}
    var showPastInfo    by remember { mutableStateOf(false) }
    var showFutureInfo  by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }

    // 3) Escuchar eventos one-shot
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                is EmotionalEvent.RegisterClicked ->
                    showModalFor = ev.date

                is EmotionalEvent.ErrorOccurred -> TODO()
            }
        }
    }

    // 4) Mostrar error en ModalInfoDialog
    LaunchedEffect(uiState) {
        if (uiState is EmotionalUiState.Error) {
            errorMessage = (uiState as EmotionalUiState.Error).msg
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
                )
            ), // Brush.verticalGradient :contentReference[oaicite:13]{index=13}
        contentAlignment = Alignment.TopCenter
    ) {
        when (uiState) {
            EmotionalUiState.Loading -> {
                Centered("Cargando…")
            }
            is EmotionalUiState.Empty -> {
                Centered("No hay registros de emociones")
            }
            is EmotionalUiState.Loaded -> {
                val daysUi = (uiState as EmotionalUiState.Loaded).days
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Título del mes
                    Text(
                        text = LocalDate.now().let {
                            it.month.getDisplayName(TextStyle.FULL, Locale("es"))
                                .replaceFirstChar { c -> c.uppercaseChar() } + " ${it.year}"
                        },
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Calendario
                    CalendarGrid(
                        month = "", // Ajustar según tu componente
                        days  = daysUi.map { dUi ->
                            EmotionDay(
                                day       = dUi.day,
                                emotionRes= dUi.iconRes,
                                onClick   = {
                                    when {
                                        dUi.day == LocalDate.now().dayOfMonth -> vm.onDayClicked(LocalDate.now().withDayOfMonth(dUi.day))
                                        dUi.day >  LocalDate.now().dayOfMonth -> showFutureInfo = true
                                        else                                   -> showPastInfo   = true
                                    }
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Enlace “SABER MÁS ?”
                    TextButtonLink("SABER MÁS ?", onClick = { /* TODO */ })

                    // Emoción más repetida
                    Text(
                        text  = "Estado emocional más repetido",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val counts = daysUi.mapNotNull { it.iconRes }.groupingBy { it }.eachCount()
                    val (icon, cnt) = counts.maxByOrNull { it.value }?.toPair() ?: (null to 0)
                    val name = emotionName(icon)
                    HabitContainer(
                        icon = {
                            ImageContainer(
                                resId = icon ?: R.drawable.iconhappyimage,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        },
                        text     = "Has estado $name durante $cnt días",
                        onClick  = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is EmotionalUiState.Error -> {
                // El contenido de error lo maneja ModalInfoDialog
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }

        // Modal de registro de emoción (AlertDialog customizado) :contentReference[oaicite:14]{index=14}
        showModalFor?.let { date ->
            RegisterEmotionalStateModal(
                date      = date,
                onConfirm = { emotion ->
                    // 1) Construye el registro con fecha y drawable
                    val record = EmotionRecord(
                        date    = date,
                        iconRes = emotion.drawableRes
                    )
                    // 2) Lanza el guardado en ViewModel
                    vm.saveEmotion(record)
                    // 3) Oculta el modal
                    showModalFor = null
                },
                onDismiss = { showModalFor = null }
            )

        }

        // Modales informativos de fecha
        if (showPastInfo) {
            ModalInfoDialog(
                visible       = true,
                icon          = Icons.Filled.Info,
                title         = "Registro fuera de fecha",
                message       = "Solo puedes registrar tu emoción para hoy.",
                primaryButton = DialogButton("Entendido") { showPastInfo = false }
            )
        }
        if (showFutureInfo) {
            ModalInfoDialog(
                visible       = true,
                icon          = Icons.Filled.Info,
                title         = "Fecha no permitida",
                message       = "No puedes registrar emociones de días futuros.",
                primaryButton = DialogButton("Entendido") { showFutureInfo = false }
            )
        }

        // Modal de error genérico
        errorMessage?.let { msg ->
            ModalInfoDialog(
                visible     = true,
                icon        = Icons.Filled.Error,
                iconColor   = MaterialTheme.colorScheme.onErrorContainer,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                title       = "Error",
                message     = msg,
                primaryButton = DialogButton("Aceptar") { errorMessage = null }
            )
        }
    }
}

/** Traduce el recurso drawable a nombre legible. */
private fun emotionName(@DrawableRes resId: Int?): String = when (resId) {
    R.drawable.iconhappyimage   -> "Feliz"
    R.drawable.iconsadimage     -> "Triste"
    R.drawable.iconcalmimage    -> "Tranquilo"
    R.drawable.iconangryimage   -> "Enojado"
    R.drawable.icondisgustimage -> "Disgustado"
    R.drawable.iconfearimage    -> "Asustado"
    else                        -> ""
}
