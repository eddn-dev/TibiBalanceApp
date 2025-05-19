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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmotionalCalendarScreen(
    vm: EmotionalCalendarViewModel = hiltViewModel()
) {
    // 0) Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    // 1) Estado del ViewModel
    val uiState by vm.ui.collectAsState()

    // 2) Estados locales
    var showModalFor   by remember { mutableStateOf<LocalDate?>(null) }
    var showPastInfo   by remember { mutableStateOf(false) }
    var showFutureInfo by remember { mutableStateOf(false) }
    var errorMessage   by remember { mutableStateOf<String?>(null) }

    // 3) Escuchar eventos one-shot
    LaunchedEffect(vm.events) {
        vm.events.collect { ev ->
            when (ev) {
                is EmotionalEvent.RegisterClicked -> {
                    showModalFor = ev.date
                }
                is EmotionalEvent.SaveCompleted -> {
                    snackbarHostState.showSnackbar(
                        "Emoción registrada para ${ev.date.dayOfMonth}/" +
                                "${ev.date.monthValue}/${ev.date.year}"
                    )
                    showModalFor = null
                }
                is EmotionalEvent.ErrorOccurred -> {
                    errorMessage = ev.message
                }
            }
        }
    }

    // 4) Capturar error de uiState
    LaunchedEffect(uiState) {
        if (uiState is EmotionalUiState.Error) {
            errorMessage = (uiState as EmotionalUiState.Error).msg
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3EA8FE).copy(alpha = .25f),
                            Color.White
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            when (uiState) {
                EmotionalUiState.Loading -> Centered("Cargando…")
                is EmotionalUiState.Empty -> Centered("No hay registros de emociones")
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
                                it.month
                                    .getDisplayName(TextStyle.FULL, Locale("es"))
                                    .replaceFirstChar { c -> c.uppercaseChar() } +
                                        " ${it.year}"
                            },
                            style = MaterialTheme.typography.titleLarge
                        )

                        // Calendario
                        CalendarGrid(
                            month = "",
                            days  = daysUi.map { dUi ->
                                EmotionDay(
                                    day        = dUi.day,
                                    emotionRes = dUi.iconRes,
                                    onClick    = {
                                        when {
                                            dUi.day == LocalDate.now().dayOfMonth ->
                                                vm.onDayClicked(
                                                    LocalDate.now()
                                                        .withDayOfMonth(dUi.day)
                                                )
                                            dUi.day > LocalDate.now().dayOfMonth ->
                                                showFutureInfo = true
                                            else ->
                                                showPastInfo = true
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // “SABER MÁS ?”
                        TextButtonLink("SABER MÁS ?", onClick = { /* TODO */ })

                        // Emoción más repetida
                        Text(
                            text  = "Estado emocional más repetido",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val counts = daysUi.mapNotNull { it.iconRes }
                            .groupingBy { it }
                            .eachCount()
                        val (icon, cnt) = counts.maxByOrNull { it.value }?.toPair()
                            ?: (null to 0)
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
                    Spacer(Modifier.fillMaxSize())
                }
            }

            // Modal de registro
            showModalFor?.let { date ->
                RegisterEmotionalStateModal(
                    date      = date,
                    onConfirm = { emotion ->
                        vm.saveEmotion(EmotionRecord(date, emotion.drawableRes))
                    },
                    onDismiss = { showModalFor = null }
                )
            }

            // Modales informativos y de error
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
            errorMessage?.let { msg ->
                ModalInfoDialog(
                    visible       = true,
                    icon          = Icons.Filled.Error,
                    iconColor     = MaterialTheme.colorScheme.onErrorContainer,
                    iconBgColor   = MaterialTheme.colorScheme.errorContainer,
                    title         = "Error",
                    message       = msg,
                    primaryButton = DialogButton("Aceptar") {
                        errorMessage = null
                        showModalFor = null
                    }
                )
            }
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
