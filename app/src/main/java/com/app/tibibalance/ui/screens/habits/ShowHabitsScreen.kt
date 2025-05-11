/**
 * @file    ShowHabitsScreen.kt
 * @ingroup ui_screens_habits // Grupo para pantallas relacionadas con hábitos
 * @brief   Define el [Composable] principal para la pantalla que muestra la lista de hábitos del usuario.
 *
 * @details
 * Esta pantalla es responsable de presentar al usuario sus hábitos actuales.
 * Gestiona diferentes estados de la UI:
 * - **Carga ([HabitsUiState.Loading]):** Muestra un indicador de progreso.
 * - **Error ([HabitsUiState.Error]):** Muestra un mensaje de error.
 * - **Vacío ([HabitsUiState.Empty]):** Se muestra cuando el usuario no tiene hábitos,
 * presentando un [EmptyState] que incita a crear el primero.
 * - **Cargado ([HabitsUiState.Loaded]):** Muestra la [HabitList] con los hábitos del usuario.
 *
 * Incluye la lógica para mostrar un modal de creación de hábitos ([AddHabitModal])
 * cuando el usuario indica que desea añadir un nuevo hábito (generalmente a través
 * de un botón de acción flotante (FAB) dentro de [HabitList] o [EmptyState]).
 *
 * La pantalla observa el estado y los eventos del [ShowHabitsViewModel] para
 * reaccionar a los cambios de datos y a las acciones del usuario.
 *
 * @see ShowHabitsViewModel ViewModel que gestiona la lógica y el estado de esta pantalla.
 * @see HabitsUiState Sealed interface que define los estados de la UI para esta pantalla.
 * @see HabitsEvent Sealed interface para eventos one-shot (como abrir el modal).
 * @see HabitList Composable que renderiza la lista de hábitos.
 * @see EmptyState Composable que se muestra cuando no hay hábitos.
 * @see AddHabitModal Composable del diálogo modal para añadir nuevos hábitos.
 * @see Centered Composable de utilidad para centrar texto (usado para Loading/Error).
 */
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.ui.components.* // Asume que HabitList, EmptyState, Centered están aquí o en subpaquetes.
import com.app.tibibalance.ui.wizard.AddHabitModal // Importa el modal del wizard.

/**
 * @brief Composable principal para la pantalla de visualización de hábitos.
 *
 * @details Esta función construye la interfaz de usuario para mostrar la lista de hábitos.
 * Reacciona a los estados emitidos por [ShowHabitsViewModel] para mostrar el contenido
 * apropiado (carga, error, lista vacía o lista de hábitos). También gestiona la
 * visibilidad del modal [AddHabitModal] para la creación de nuevos hábitos.
 *
 * @param vm La instancia de [ShowHabitsViewModel] (inyectada por Hilt) que proporciona
 * la lógica de negocio y el estado de la UI para esta pantalla.
 */
@Composable
fun ShowHabitsScreen(
    vm: ShowHabitsViewModel = hiltViewModel()
) {
    /* ---------- STATE LOCAL PARA EL MODAL ---------- */
    // Controla si el modal para añadir un nuevo hábito debe mostrarse.
    var showAddModal by remember { mutableStateOf(false) }

    /* ---------- OBSERVAR STATE DEL VIEWMODEL ---------- */
    // Recolecta el estado de la UI emitido por el ViewModel.
    val uiState by vm.ui.collectAsState()

    /* ---------- ONE-SHOT EVENTS (manejo de eventos del ViewModel) ---------- */
    // `LocalContext.current` se podría usar aquí si se necesitara para algún evento,
    // pero en este caso no es indispensable para el evento AddClicked.
    // val context = LocalContext.current
    LaunchedEffect(Unit) { // Se lanza una vez cuando el Composable entra en la composición.
        vm.events.collect { ev -> // Escucha los eventos emitidos por el ViewModel.
            when (ev) {
                // Si el evento es AddClicked, activa la visibilidad del modal.
                HabitsEvent.AddClicked -> showAddModal = true
            }
        }
    }

    /* ---------- FONDO GRADIENTE ---------- */
    // Define un fondo degradado común para la pantalla.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor Box principal que ocupa toda la pantalla y aplica el fondo.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Renderiza el contenido basado en el estado actual de la UI.
        when (val currentState = uiState) { // Captura el estado para evitar recomposiciones innecesarias
            HabitsUiState.Loading -> Centered("Cargando…") // Muestra "Cargando..."
            is HabitsUiState.Error -> Centered(currentState.msg) // Muestra el mensaje de error.
            HabitsUiState.Empty -> EmptyState(onAdd = vm::onAddClicked) // Muestra estado vacío con botón para añadir.
            is HabitsUiState.Loaded -> HabitList(
                habits  = currentState.data,

                // ✔  lambda con los 2 parámetros
                onCheck = { _, _ -> /* TODO */ },

                onEdit  = { habit ->
                    /* TODO: navegación a edición */
                },
                onAdd   = vm::onAddClicked
            )

        }
    }

    /* ---------- MODAL “AGREGAR HÁBITO” ---------- */
    // Muestra el modal si showAddModal es true.
    if (showAddModal) {
        AddHabitModal(
            // Callback para cuando el modal se cierra (e.g., por el usuario).
            onDismissRequest = { showAddModal = false }
        )
    }
}