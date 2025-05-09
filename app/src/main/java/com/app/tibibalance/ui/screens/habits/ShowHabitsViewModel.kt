/**
 * @file    ShowHabitsViewModel.kt
 * @ingroup ui_screens_habits // Grupo para ViewModels de pantallas de hábitos
 * @brief   ViewModel para la pantalla que muestra la lista de hábitos del usuario ([ShowHabitsScreen]).
 *
 * @details
 * Este ViewModel, gestionado por Hilt (`@HiltViewModel`), se encarga de:
 * 1.  Observar los hábitos del usuario desde el [HabitRepository].
 * 2.  Mapear los datos del dominio (potencialmente `List<Habit>`) a un modelo
 * simplificado para la UI ([HabitUi]).
 * 3.  Exponer el estado de la UI ([HabitsUiState]) a través de un [StateFlow] `ui`,
 * manejando los casos de carga, lista vacía, lista cargada y errores.
 * 4.  Gestionar eventos one-shot ([HabitsEvent]) a través de un [SharedFlow] `events`,
 * específicamente para indicar cuándo el usuario ha solicitado añadir un nuevo hábito.
 *
 * **Nota:** La versión actual utiliza un flujo de datos simulado (`habitsMock`)
 * para fines de desarrollo o demostración. En una implementación completa, esto
 * se reemplazaría con una llamada real al `repo.observeHabits()`.
 *
 * @property ui Flujo de estado inmutable ([StateFlow]) que la [ShowHabitsScreen] observa
 * para reaccionar a los cambios de estado ([HabitsUiState]).
 * @property events Flujo compartido ([SharedFlow]) que emite eventos one-shot ([HabitsEvent])
 * a la UI (e.g., para disparar la apertura de un modal).
 *
 * @see ShowHabitsScreen Composable de la pantalla que utiliza este ViewModel.
 * @see HabitsUiState Sealed interface que define los estados de la UI.
 * @see HabitsEvent Sealed interface para eventos one-shot.
 * @see HabitUi Data class que representa un hábito en la lista de la UI.
 * @see HabitRepository Repositorio (actualmente simulado) de donde se obtendrían los datos de hábitos.
 * @see dagger.hilt.android.lifecycle.HiltViewModel
 * @see androidx.lifecycle.viewModelScope
 * @see kotlinx.coroutines.flow.StateFlow
 * @see kotlinx.coroutines.flow.SharedFlow
 */
package com.app.tibibalance.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.tibibalance.data.repository.HabitRepository   // ← Import necesario cuando se use el repo real
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @brief Data class que representa la información mínima necesaria para mostrar
 * un hábito en una fila de la lista en [ShowHabitsScreen].
 *
 * @param id Identificador único del hábito.
 * @param name Nombre visible del hábito.
 * @param icon Nombre del icono Material (e.g., "LocalDrink") asociado al hábito.
 * @param checked Estado booleano que indica si el hábito ha sido marcado como completado
 * hoy (o en el periodo relevante). Por defecto `false`.
 */
data class HabitUi(
    val id      : String,
    val name    : String,
    val icon    : String,       // nombre Material, ej. "LocalDrink"
    val checked : Boolean = false
)

/**
 * @brief Interfaz sellada que define los diferentes estados posibles para la
 * pantalla que muestra la lista de hábitos ([ShowHabitsScreen]).
 */
sealed interface HabitsUiState {
    /** @brief Estado inicial o mientras se cargan los datos por primera vez. */
    data object Loading               : HabitsUiState
    /** @brief Estado que indica que el usuario no tiene ningún hábito registrado. */
    data object Empty                 : HabitsUiState
    /**
     * @brief Estado que indica que los hábitos se han cargado correctamente.
     * @param data La lista de [HabitUi] a mostrar.
     */
    data class Loaded(val data: List<HabitUi>) : HabitsUiState
    /**
     * @brief Estado que indica que ocurrió un error al cargar los hábitos.
     * @param msg Mensaje descriptivo del error.
     */
    data class Error (val msg: String)         : HabitsUiState
}

/**
 * @brief Interfaz sellada para eventos únicos que el ViewModel envía a la UI.
 * @details Se utiliza para comunicar acciones que no forman parte del estado persistente,
 * como disparar la navegación o mostrar diálogos.
 */
sealed interface HabitsEvent {
    /** @brief Evento que indica que el usuario ha pulsado el botón para añadir un nuevo hábito. */
    data object AddClicked : HabitsEvent
}

/**
 * @brief ViewModel para la pantalla [ShowHabitsScreen].
 *
 * @details Gestiona la carga de los hábitos, mapea los datos al formato [HabitUi],
 * expone el estado [HabitsUiState] a la pantalla y maneja la emisión del evento
 * [HabitsEvent.AddClicked].
 *
 * @constructor Inyecta [HabitRepository] mediante Hilt (actualmente simulado).
 * @param repo El repositorio de hábitos (debería ser la interfaz [HabitRepository]).
 */
@HiltViewModel
class ShowHabitsViewModel @Inject constructor(
    repo: HabitRepository // Inyección del repositorio (actualmente no usado por el mock)
) : ViewModel() {

    /** Flujo mutable interno para emitir eventos one-shot. */
    private val _events = MutableSharedFlow<HabitsEvent>()
    /** Flujo compartido expuesto a la UI para observar eventos one-shot. */
    val events: SharedFlow<HabitsEvent> = _events.asSharedFlow()

    /**
     * @brief Flujo simulado que emite una lista de hábitos.
     * @details **Reemplazar con `repo.observeHabits().map { domainList -> domainList.map { it.toHabitUi() } }`**
     * en una implementación real. Actualmente, solo emite una lista vacía.
     * Se incluye un comentario con código para simular un estado cargado tras un delay.
     */
    private val habitsMock: Flow<List<HabitUi>> = flow {
        emit(emptyList<HabitUi>())            // Estado inicial: lista vacía.
        // Para probar estado cargado, descomentar y añadir datos:
        // kotlinx.coroutines.delay(1000)
        // emit(listOf(
        //     HabitUi("h1", "Beber 2L Agua", "LocalDrink", false),
        //     HabitUi("h2", "Dormir 8h", "Bedtime", true)
        // ))
    }

    /**
     * @brief Flujo de estado ([StateFlow]) que expone el [HabitsUiState] actual a la UI.
     * @details Transforma el flujo de datos (`habitsMock` o el flujo real del repositorio)
     * en el estado de UI correspondiente (Empty, Loaded, Error). Se manejan errores
     * del flujo fuente y se inicia con el estado `Loading`.
     */
    val ui: StateFlow<HabitsUiState> =
        habitsMock // <- Reemplazar con el flujo del repositorio mapeado a List<HabitUi>
            .map<List<HabitUi>, HabitsUiState> { habitList -> // Mapea la lista a un estado
                if (habitList.isEmpty()) HabitsUiState.Empty // Si está vacía -> Empty
                else HabitsUiState.Loaded(habitList) // Si tiene datos -> Loaded
            }
            .catch { throwable -> // Captura errores del flujo fuente
                emit(HabitsUiState.Error(throwable.message ?: "Error desconocido al cargar hábitos"))
            }
            // Convierte el Flow en un StateFlow, compartiendo la última emisión
            // y manteniendo el estado mientras haya suscriptores (Eagerly inicia la colección inmediatamente).
            // El valor inicial mientras se establece la conexión/carga es Loading.
            .stateIn(viewModelScope, SharingStarted.Eagerly, HabitsUiState.Loading)

    /**
     * @brief Emite el evento [HabitsEvent.AddClicked] al flujo `_events`.
     * @details Se llama desde la UI ([ShowHabitsScreen]) cuando el usuario pulsa
     * el botón de añadir hábito, para que la UI pueda reaccionar abriendo el modal
     * de creación. Se ejecuta en el `viewModelScope`.
     */
    fun onAddClicked() = viewModelScope.launch { _events.emit(HabitsEvent.AddClicked) }

    // TODO: Añadir funciones para manejar la lógica de onCheck y onEdit cuando se implementen.
    // fun toggleHabit(id: String, isChecked: Boolean) { ... }
    // fun onEditHabitClicked(id: String) { ... }
}

// TODO: Añadir función de extensión Habit.toHabitUi() si es necesario para mapear
//       el modelo de dominio al modelo de UI.
// fun Habit.toHabitUi(): HabitUi = HabitUi(id = this.id, name = this.name, icon = this.icon /*, checked = calcularCheckedHoy() */ )