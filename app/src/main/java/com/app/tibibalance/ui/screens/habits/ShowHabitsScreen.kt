package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.wizard.AddHabitModal

@Composable
fun ShowHabitsScreen(
    vm: ShowHabitsViewModel = hiltViewModel()
) {
    /* ---------- STATE LOCAL PARA EL MODAL ---------- */
    var showAddModal by remember { mutableStateOf(false) }

    /* ---------- OBSERVAR STATE DEL VIEWMODEL ---------- */
    val uiState by vm.ui.collectAsState()

    /* ---------- ONE-SHOT EVENTS ---------- */
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                HabitsEvent.AddClicked -> showAddModal = true   // ← abre el modal
            }
        }
    }

    /* ---------- FONDO GRADIENTE ---------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        when (uiState) {
            HabitsUiState.Loading -> Centered("Cargando…")
            is HabitsUiState.Error -> Centered((uiState as HabitsUiState.Error).msg)

            HabitsUiState.Empty -> EmptyState(onAdd = vm::onAddClicked)

            is HabitsUiState.Loaded -> HabitList(
                habits  = (uiState as HabitsUiState.Loaded).data,
                onCheck = { /* TODO: update repo */ } as (HabitUi, Boolean) -> Unit,
                onEdit  = { /* TODO: editor */ },
                onAdd   = vm::onAddClicked             // botón FAB o similar
            )
        }
    }

    /* ---------- MODAL “AGREGAR HÁBITO” ---------- */
    if (showAddModal) {
        AddHabitModal(
            onDismissRequest = { showAddModal = false }        // ← cierra el modal
        )
    }
}
