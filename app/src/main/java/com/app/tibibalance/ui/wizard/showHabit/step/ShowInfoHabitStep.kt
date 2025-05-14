/* ui/wizard/showHabit/step/ShowInfoHabitStep.kt */
package com.app.tibibalance.ui.wizard.showHabit.step

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.Habit
import com.app.tibibalance.ui.components.RoundedIconButton
import com.app.tibibalance.ui.components.inputs.iconByName
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title

/**
 * Paso SOLO-lectura que muestra toda la información disponible del hábito.
 * Por ahora: icono, nombre, descripción, categoría, challenge y notificaciones.
 */
@Composable
fun ShowInfoHabitStep(
    habit      : Habit,
    onEditClick: () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    /* Icono grande */
    Icon(
        imageVector       = iconByName(habit.icon),
        contentDescription = null,
        modifier          = Modifier.size(72.dp),
        tint              = MaterialTheme.colorScheme.primary
    )

    /* Nombre */
    Title(
        text      = habit.name,
        modifier  = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    /* Descripción (si existe) */
    if (habit.description.isNotBlank()) {
        Description(
            text      = habit.description,
            modifier  = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }

    /* Datos extra */
    Text(
        text = buildString {
            append("Categoría: ${habit.category.display}\n")
            append("Frecuencia: ${habit.repeatPreset.name}\n")
            append("Sesión: ${habit.session}\n")
            append("Periodo: ${habit.period}\n")
            if (habit.notifConfig.enabled)
                append("Notificaciones activas (${habit.notifConfig.timesOfDay.joinToString()})\n")
            if (habit.challenge)
                append("🔒 Reto activo")
        },
        style    = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )

    /* Botón Editar (placeholder) */
    RoundedIconButton(
        onClick            = onEditClick,
        icon               = Icons.Default.Edit,
        contentDescription = "Editar hábito"
    )
}
