/* ui/wizard/showHabit/ShowHabitModal.kt */
package com.app.tibibalance.ui.wizard.showHabit

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.ui.components.ModalContainer
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
import com.app.tibibalance.ui.wizard.showHabit.step.ShowInfoHabitStep

/**
 * Modal (wizard) de visualización / edición.
 * Por ahora solo un paso en modo lectura.
 */
@Composable
fun ShowHabitModal(
    habitId          : String,
    onDismissRequest : () -> Unit,
    vm               : ShowHabitViewModel = hiltViewModel()
) {
    /* ── cada vez que cambia el id se lo decimos al VM ───────── */
    LaunchedEffect(habitId) { vm.load(habitId) }

    val ui by vm.ui.collectAsState()

    ModalContainer(onDismissRequest, closeButtonEnabled = true) {
        when (ui) {
            ShowHabitUiState.Loading ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    Alignment.Center
                ) { CircularProgressIndicator() }

            is ShowHabitUiState.Error ->
                ModalInfoDialog(
                    visible = true,
                    title   = "Error",
                    message = (ui as ShowHabitUiState.Error).msg
                )

            is ShowHabitUiState.Info ->
                ShowInfoHabitStep(
                    habit       = (ui as ShowHabitUiState.Info).habit,
                    onEditClick = { /* TODO: lanzar modo edición */ }
                )
        }
    }
}
