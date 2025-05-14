/**
 * @file BasicInfoStep.kt
 * @ingroup ui_wizard_step
 * @brief Define el Composable para el paso de "Información básica" en el asistente de creación/edición de hábitos.
 *
 * @details
 * Este Composable representa la primera pantalla (o un paso intermedio) dentro de un
 * flujo tipo asistente (`wizard`) para que el usuario introduzca los detalles
 * fundamentales de un nuevo hábito. Incluye campos para:
 * - Selección de icono ([InputIcon]).
 * - Nombre del hábito ([InputText]), que es obligatorio.
 * - Descripción opcional del hábito ([InputText] multilínea).
 * - Selección de categoría ([InputSelect]).
 *
 * Utiliza `rememberSaveable` con un [HabitFormSaver] personalizado para mantener el estado
 * del formulario ([HabitForm]) a través de cambios de configuración o interrupciones del proceso.
 * Los cambios en el formulario local se propagan hacia el ViewModel padre a través del
 * callback `onFormChange`.
 *
 * Recibe una lista de errores (`errors`) del ViewModel para mostrar feedback de validación
 * específico del campo (actualmente solo para el nombre).
 *
 * @see com.app.tibibalance.ui.wizard.createHabit.AddHabitViewModel ViewModel que probablemente gestiona el estado y la lógica de este asistente.
 * @see HabitForm Data class que representa el estado del formulario.
 * @see HabitFormSaver Saver para preservar el estado de HabitForm.
 * @see InputIcon Componente para seleccionar el icono.
 * @see InputText Componente reutilizable para campos de texto (nombre, descripción).
 * @see InputSelect Componente reutilizable para el selector de categoría.
 * @see Title Componente para el título de la pantalla.
 * @see HabitCategory Enum que define las categorías disponibles.
 */
package com.app.tibibalance.ui.wizard.createHabit.step

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.HabitForm
import com.app.tibibalance.domain.model.HabitCategory
import com.app.tibibalance.ui.components.inputs.*
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.wizard.createHabit.HabitFormSaver
import com.app.tibibalance.ui.components.inputs.InputIcon
import com.app.tibibalance.ui.wizard.createHabit.BasicError

/**
 * @brief Composable que define la interfaz de usuario para el paso de "Información básica" del asistente de hábitos.
 *
 * @param initial El estado inicial del formulario ([HabitForm]) al entrar en este paso.
 * Se utiliza para inicializar el estado local `form`.
 * @param errors Una lista de [String] que contiene los mensajes de error de validación
 * relacionados con los campos de este paso (e.g., "El nombre es obligatorio").
 * @param onFormChange Callback que se invoca cada vez que el estado local `form` cambia,
 * permitiendo al ViewModel padre mantenerse actualizado. Recibe el [HabitForm] modificado.
 * @param onBack Callback opcional que se invocaría si este paso tuviera un botón "Atrás" explícito
 * (actualmente no lo tiene, la navegación atrás es manejada por el contenedor del wizard).
 * Por defecto, es una lambda vacía.
 */
@Composable
fun BasicInfoStep(
    initial      : HabitForm,
    errors       : List<BasicError>,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit = {} // Callback onBack definido pero no usado internamente
) {
    /* ---------- Estado Local Serializable ---------- */
    // 'form' mantiene el estado actual de los campos de entrada de este paso.
    // Se inicializa con 'initial' y se guarda/restaura usando HabitFormSaver.
    var form by rememberSaveable(stateSaver = HabitFormSaver) { mutableStateOf(initial) }
    // Efecto que se dispara cada vez que 'form' cambia, notificando al ViewModel.
    LaunchedEffect(form) { onFormChange(form) }

    /* ---------- Flags de Error ---------- */
    // Determina si hay un error relacionado con el campo 'nombre' en la lista 'errors'.
    val nameErr = remember(errors) { // Remember para evitar recálculo innecesario
        errors.contains(BasicError.NameRequired)
    }

    // Columna principal que permite desplazamiento vertical y aplica padding.
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el espacio disponible verticalmente.
            .verticalScroll(rememberScrollState()) // Permite el scroll si el contenido excede la altura.
            .padding(horizontal = 12.dp, vertical = 16.dp), // Padding alrededor del contenido.
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio vertical entre elementos.
    ) {
        /* Título de la pantalla */
        Title(
            text      = "Información básica",
            modifier  = Modifier.fillMaxWidth(), // Ocupa todo el ancho.
            textAlign = TextAlign.Center // Texto centrado.
        )

        /* Selector de Icono */
        InputIcon(
            iconName    = form.icon, // Icono actual del formulario.
            onChange    = { newIconName -> form = form.copy(icon = newIconName) }, // Actualiza el estado 'form'.
            description = "Icono", // Descripción para accesibilidad.
            modifier    = Modifier.align(Alignment.CenterHorizontally), // Centra el icono.
            isEditing   = true // Permite la edición/selección del icono.
        )

        /* Campo: Nombre del Hábito */
        InputText(
            value           = form.name, // Valor actual del nombre.
            onValueChange   = { newName -> form = form.copy(name = newName) }, // Actualiza el estado.
            placeholder     = "Nombre del hábito *", // Placeholder y etiqueta.
            isError         = nameErr, // Indica si hay error en este campo.
            // Muestra "Obligatorio" si hay error, sino null (lo que permite mostrar el contador si maxChars está activo).
            supportingText  = if (nameErr) "Obligatorio" else null,
            maxChars        = 30, // Límite de caracteres.
            modifier        = Modifier.fillMaxWidth() // Ocupa todo el ancho.
        )

        /* Campo: Descripción del Hábito */
        InputText(
            value         = form.desc, // Valor actual de la descripción.
            onValueChange = { newDesc -> form = form.copy(desc = newDesc) }, // Actualiza el estado.
            singleLine    = false, // Permite múltiples líneas.
            placeholder   = "Descripción", // Placeholder y etiqueta.
            maxChars      = 140, // Límite de caracteres.
            modifier      = Modifier
                .fillMaxWidth() // Ocupa todo el ancho.
                .heightIn(min = 96.dp) // Altura mínima para el campo multilínea.
        )

        /* Campo: Selector de Categoría */
        InputSelect(
            label            = "Categoría *", // Etiqueta del selector.
            // Genera la lista de opciones a partir de los valores del Enum HabitCategory, usando su propiedad 'display'.
            options          = remember { HabitCategory.entries.map { it.display } },
            selectedOption   = form.category.display, // Opción actualmente seleccionada (mostrando su 'display').
            onOptionSelected = { selectedDisplay -> // Callback cuando se selecciona una opción.
                // Encuentra el Enum correspondiente al 'display' seleccionado y actualiza el estado 'form'.
                form = form.copy(
                    category = HabitCategory.entries.first { it.display == selectedDisplay }
                )
            }
            // No se pasa isError ni supportingText a InputSelect en este caso.
        )
    }
}