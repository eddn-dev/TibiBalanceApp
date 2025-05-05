// ui/screens/habits/ShowHabitsScreen.kt
package com.app.tibibalance.ui.screens.habits

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ShowHabitsScreen(
    vm: ShowHabitsViewModel = hiltViewModel()
) {
    val uiState by vm.ui.collectAsState()
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    /* ------------ escucha one-shot events ------------ */
    LaunchedEffect(Unit) {
        vm.events.collect { ev ->
            when (ev) {
                HabitsEvent.AddClicked -> {
                    /* aquí abrirás el modal de creación más adelante */
                    Toast.makeText(context, "Abrir modal (pendiente)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /* ------------ fondo degradado ------------ */
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
                habits = (uiState as HabitsUiState.Loaded).data,
                onCheck = { /* TODO: update repo */ } as (HabitUi, Boolean) -> Unit,
                onEdit  = { /* TODO: editor */ },
                onAdd   = vm::onAddClicked
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewShowHabitsScreen() {
    ShowHabitsScreen()
}
