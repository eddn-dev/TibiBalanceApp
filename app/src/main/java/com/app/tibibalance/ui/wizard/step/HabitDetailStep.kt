/* ui/wizard/step/HabitDetailsStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.wizard.HabitForm

/**
 * Paso 1 del wizard – Detalles del hábito.
 * No muestra botones de navegación; solo notifica a la VM cuando algo cambia.
 */
@Composable
fun HabitDetailsStep(
    initial      : HabitForm,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit    // <- lo sigue recibiendo para la top-bar, aunque aquí no se usa
) {
    /* --- Estado local con rememberSaveable para sobrevivir configuraciones --- */
    var name   by rememberSaveable { mutableStateOf(initial.name) }
    var desc   by rememberSaveable { mutableStateOf(initial.desc) }
    var freq   by rememberSaveable { mutableStateOf(initial.freq) }
    var cat    by rememberSaveable { mutableStateOf(initial.category) }
    var notify by rememberSaveable { mutableStateOf(initial.notify) }

    /* Propagamos los cambios a la VM */
    LaunchedEffect(name, desc, freq, cat, notify) {
        onFormChange(HabitForm(name, desc, freq, cat, notify))
    }

    /* --- UI --- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        /* Icono placeholder */
        ImageContainer(
            resId              = R.drawable.addiconimage,
            contentDescription = "Icono del hábito",
            modifier           = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )

        /* Nombre */
        InputText(
            value         = name,
            onValueChange = { name = it },
            placeholder         = "Nombre del hábito *",
            maxChars      = 30,
            modifier      = Modifier.fillMaxWidth()
        )

        /* Descripción (multilínea) */
        InputText(
            value         = desc,
            onValueChange = { desc = it },
            placeholder         = "Descripción",
            maxChars      = 140,
            modifier      = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )

        /* Frecuencia */
        InputSelect(
            options          = listOf("Diario", "Semanal", "Mensual"),
            selectedOption   = freq,
            onOptionSelected = { freq = it },
            label            = "Frecuencia *",
        )

        /* Categoría */
        InputSelect(
            options          = listOf("Salud", "Productividad", "Bienestar"),
            selectedOption   = cat,
            onOptionSelected = { cat = it },
            label            = "Categoría *",
        )

        /* Switch de notificación */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { notify = !notify },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notificarme", style = MaterialTheme.typography.bodyMedium)
            SwitchToggle(
                checked          = notify,
                onCheckedChange  = { notify = it }
            )
        }
    }
}
