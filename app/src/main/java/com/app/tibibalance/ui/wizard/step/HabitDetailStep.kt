/* ui/wizard/step/HabitDetailsStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.*
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.inputs.InputIcon
import com.app.tibibalance.ui.components.inputs.InputSelect
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.wizard.HabitFormSaver

/**
 * Paso 1 del wizard – Detalles del hábito.
 * No muestra botones de navegación; simplemente notifica a la VM en caliente.
 */
@Composable
fun HabitDetailsStep(
    initial      : HabitForm,
    errors       : List<String>,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit
) {
    var form by rememberSaveable(stateSaver = HabitFormSaver) { mutableStateOf(initial) }

    /* ➊ Re-emite los cambios hacia la VM */
    LaunchedEffect(form) { onFormChange(form) }

    /* ➋ Mapeo de errores */
    val nameErr       = errors.any { it.contains("nombre",  true) }
    val sessionQtyErr = errors.any { it.contains("duración", true) }
    val periodQtyErr  = errors.any { it.contains("periodo",   true) }

    /* ---------- UI ---------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        /* --- Título de la pantalla --- */
        Title(
            text = "Detalles del hábito",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        InputIcon(
            iconName    = form.icon,
            onChange    = { form = form.copy(icon = it) },
            modifier    = Modifier.align(Alignment.CenterHorizontally),
            description = "Icono",          // texto bajo el botón
            isEditing   = true              // o false si fuese sólo lectura
        )

        /* --- Nombre (obligatorio) --- */
        InputText(
            value           = form.name,
            onValueChange   = { form = form.copy(name = it) },
            placeholder     = "Nombre del hábito *",
            isError         = nameErr,
            supportingText  = if (nameErr) "Obligatorio" else null,
            maxChars        = 30,
            modifier        = Modifier.fillMaxWidth()
        )

        /* --- Descripción --- */
        InputText(
            value         = form.desc,
            onValueChange = { form = form.copy(desc = it) },
            placeholder   = "Descripción",
            maxChars      = 140,
            modifier      = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
        )

        /* --- Categoría --- */
        InputSelect(
            label = "Categoría *",
            options = HabitCategory.entries.map { it.display },
            selectedOption = form.category.display,
            onOptionSelected = { disp ->
                form = form.copy(
                    category = HabitCategory.entries.first { it.display == disp }
                )
            }
        )

        /* --- Duración de la actividad --- */
        Text("Duración de la actividad", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(form.sessionUnit != SessionUnit.INDEFINIDO) {
                InputText(
                    value           = form.sessionQty?.toString().orEmpty(),
                    onValueChange   = { form = form.copy(sessionQty = it.toIntOrNull()) },
                    placeholder     = "Cantidad",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError         = sessionQtyErr,
                    supportingText  = if (sessionQtyErr) "Requerido" else null,
                    modifier        = Modifier.width(120.dp)
                )
            }

            InputSelect(
                options          = listOf("Indefinido", "Minutos", "Horas"),
                selectedOption   = when (form.sessionUnit) {
                    SessionUnit.MINUTOS    -> "Minutos"
                    SessionUnit.HORAS      -> "Horas"
                    else                   -> "Indefinido"
                },
                onOptionSelected = { sel ->
                    val unit = when (sel) {
                        "Minutos" -> SessionUnit.MINUTOS
                        "Horas"   -> SessionUnit.HORAS
                        else      -> SessionUnit.INDEFINIDO
                    }
                    form = form.copy(
                        sessionUnit = unit,
                        sessionQty  = form.sessionQty.takeIf { unit != SessionUnit.INDEFINIDO }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        /* --- Repetición --- */
        InputSelect(
            label          = "Repetir hábito",
            options        = listOf(
                "Indefinido", "Diariamente", "Cada 3 días",
                "Semanalmente", "Cada 15 días", "Mensualmente"
            ),
            selectedOption = when (form.repeatPattern) {
                RepeatPattern.DIARIO         -> "Diariamente"
                RepeatPattern.CADA_3_DIAS    -> "Cada 3 días"
                RepeatPattern.SEMANALMENTE   -> "Semanalmente"
                RepeatPattern.CADA_15_DIAS   -> "Cada 15 días"
                RepeatPattern.MENSUALMENTE   -> "Mensualmente"
                else                         -> "Indefinido"
            },
            onOptionSelected = { sel ->
                form = form.copy(
                    repeatPattern = when (sel) {
                        "Diariamente"  -> RepeatPattern.DIARIO
                        "Cada 3 días"  -> RepeatPattern.CADA_3_DIAS
                        "Semanalmente" -> RepeatPattern.SEMANALMENTE
                        "Cada 15 días" -> RepeatPattern.CADA_15_DIAS
                        "Mensualmente" -> RepeatPattern.MENSUALMENTE
                        else           -> RepeatPattern.INDEFINIDO
                    }
                )
            }
        )

        /* --- Periodo total --- */
        Text("Periodo del hábito", style = MaterialTheme.typography.bodyMedium)
        Row(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(form.periodUnit != PeriodUnit.INDEFINIDO) {
                InputText(
                    value           = form.periodQty?.toString().orEmpty(),
                    onValueChange   = { form = form.copy(periodQty = it.toIntOrNull()) },
                    placeholder     = "Cantidad",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError         = periodQtyErr,
                    supportingText  = if (periodQtyErr) "Requerido" else null,
                    modifier        = Modifier.width(120.dp)
                )
            }

            InputSelect(
                options          = listOf("Indefinido", "Días", "Semanas", "Meses"),
                selectedOption   = when (form.periodUnit) {
                    PeriodUnit.DIAS    -> "Días"
                    PeriodUnit.SEMANAS -> "Semanas"
                    PeriodUnit.MESES   -> "Meses"
                    else               -> "Indefinido"
                },
                onOptionSelected = { sel ->
                    val unit = when (sel) {
                        "Días"     -> PeriodUnit.DIAS
                        "Semanas"  -> PeriodUnit.SEMANAS
                        "Meses"    -> PeriodUnit.MESES
                        else       -> PeriodUnit.INDEFINIDO
                    }
                    form = form.copy(
                        periodUnit = unit,
                        periodQty  = form.periodQty.takeIf { unit != PeriodUnit.INDEFINIDO }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        /* --- Switch de notificación --- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { form = form.copy(notify = !form.notify) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Notificarme", style = MaterialTheme.typography.bodyMedium)
            SwitchToggle(
                checked         = form.notify,
                onCheckedChange = { form = form.copy(notify = it) }
            )
        }
    }
}

