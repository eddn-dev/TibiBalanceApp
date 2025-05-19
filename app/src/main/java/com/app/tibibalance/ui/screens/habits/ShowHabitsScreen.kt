/* ui/screens/habits/ShowHabitsScreen.kt */
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.ui.components.*            // HabitList, EmptyState, Centered…
import com.app.tibibalance.ui.wizard.createHabit.AddHabitModal
import com.app.tibibalance.ui.wizard.showHabit.ShowHabitModal
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog

@Composable
fun ShowHabitsScreen(
    vm: ShowHabitsViewModel = hiltViewModel()
) {
    /* ---------- STATE LOCAL PARA MODALES ---------- */
    var showAddModal     by remember { mutableStateOf(false) }
    var showDetailsModal by remember { mutableStateOf(false) }
    var selectedId       by remember { mutableStateOf<String?>(null) }
    var showHabitsHelp by remember { mutableStateOf(false) }

    /* ---------- OBSERVAR STATE DEL VIEWMODEL ---------- */
    val uiState by vm.ui.collectAsState()

    /* ---------- EVENTOS ONE-SHOT ---------- */
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                HabitsEvent.AddClicked     -> showAddModal = true
                is HabitsEvent.ShowDetails -> {
                    selectedId       = ev.habitId        // ← correcto
                    showDetailsModal = true
                }
            }
        }
    }

    /* ---------- UI PRINCIPAL ---------- */
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        when (val st = uiState) {
            HabitsUiState.Loading -> Centered("Cargando…")
            is HabitsUiState.Error -> Centered(st.msg)
            HabitsUiState.Empty    -> EmptyState(onAdd = vm::onAddClicked)
            is HabitsUiState.Loaded -> {
                Column {
                    HabitList(
                        habits  = st.data,
                        onCheck = { _, _ -> /* TODO */ },
                        onEdit  = vm::onHabitClicked,   // ← muestra detalles
                        onAdd   = vm::onAddClicked
                    )
                    TextButtonLink(
                        text = "SABER MÁS ?",
                        onClick = { showHabitsHelp = true },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    /* ---------- MODAL NUEVO HÁBITO ---------- */
    if (showAddModal) {
        AddHabitModal(onDismissRequest = { showAddModal = false })
    }

    /* ---------- MODAL DETALLES HÁBITO ---------- */
    if (showDetailsModal && selectedId != null) {
        ShowHabitModal(
            habitId          = selectedId!!,
            onDismissRequest = { showDetailsModal = false }
        )
    }

    if (showHabitsHelp) {
        ModalInfoDialog(
            visible = true,
            icon = Icons.Default.Info,
            title = "¿Cómo usar esta sección?",
            message = "Aquí puedes ver tus hábitos diarios, tanto de salud como personalizados. Puedes marcarlos como completados y agregar nuevos según lo necesites. Este registro te ayudará a mantener tu progreso y motivación.",
            primaryButton = DialogButton("Entendido") {
                showHabitsHelp = false
            }
        )
    }
}
