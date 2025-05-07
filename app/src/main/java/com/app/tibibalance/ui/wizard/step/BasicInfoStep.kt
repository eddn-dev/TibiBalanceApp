package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.app.tibibalance.ui.wizard.HabitFormSaver
import com.app.tibibalance.ui.components.inputs.InputIcon

@Composable
fun BasicInfoStep(
    initial      : HabitForm,
    errors       : List<String>,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit = {}
) {
    /* ---------- estado local serializable ---------- */
    var form by rememberSaveable(stateSaver = HabitFormSaver) { mutableStateOf(initial) }
    LaunchedEffect(form) { onFormChange(form) }

    /* ---------- flags de error ---------- */
    val nameErr = errors.any { it.contains("nombre", ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* título */
        Title(
            text      = "Información básica",
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        /* icono */
        InputIcon(
            iconName    = form.icon,
            onChange    = { form = form.copy(icon = it) },
            description = "Icono",
            modifier    = Modifier.align(Alignment.CenterHorizontally),
            isEditing   = true
        )

        /* nombre */
        InputText(
            value           = form.name,
            onValueChange   = { form = form.copy(name = it) },
            placeholder     = "Nombre del hábito *",
            isError         = nameErr,
            supportingText  = if (nameErr) "Obligatorio" else null,
            maxChars        = 30,
            modifier        = Modifier.fillMaxWidth()
        )

        /* descripción */
        InputText(
            value         = form.desc,
            onValueChange = { form = form.copy(desc = it) },
            singleLine    = false,
            placeholder   = "Descripción",
            maxChars      = 140,
            modifier      = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
        )

        /* categoría */
        InputSelect(
            label            = "Categoría *",
            options          = HabitCategory.entries.map { it.display },
            selectedOption   = form.category.display,
            onOptionSelected = { disp ->
                form = form.copy(
                    category = HabitCategory.entries.first { it.display == disp }
                )
            }
        )
    }
}
