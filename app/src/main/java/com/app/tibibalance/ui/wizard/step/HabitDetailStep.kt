/* ui/wizard/step/HabitDetailsStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.*
import com.app.tibibalance.ui.components.*
import kotlinx.coroutines.launch

/**
 * Paso 1 del wizard – Detalles del hábito.
 * No muestra botones de navegación; simplemente notifica a la VM en caliente.
 */
@Composable
fun HabitDetailsStep(
    initial      : HabitForm,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit
) {
    /* Un solo objeto que Compose puede serializar (enums + primitivos). */
    var form by rememberSaveable { mutableStateOf(initial) }

    /* Propagar a la VM cada modificación local */
    LaunchedEffect(form) { onFormChange(form) }

    /* ---------- UI ---------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        /* Icono placeholder (future: picker) */
        ImageContainer(
            resId              = R.drawable.addiconimage,
            contentDescription = "Icono del hábito",
            modifier           = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        )

        /* --------- Nombre --------- */
        InputText(
            value         = form.name,
            onValueChange = { form = form.copy(name = it) },
            placeholder   = "Nombre del hábito *",
            maxChars      = 30,
            modifier      = Modifier.fillMaxWidth()
        )

        /* --------- Descripción --------- */
        InputText(
            value         = form.desc,
            onValueChange = { form = form.copy(desc = it) },
            placeholder   = "Descripción",
            maxChars      = 140,
            modifier      = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )

        /* --------- Categoría --------- */
        InputSelect(
            label            = "Categoría *",
            options          = listOf("Salud", "Productividad", "Bienestar"),
            selectedOption   = form.category,
            onOptionSelected = { form = form.copy(category = it) }
        )

        /* --------- Tiempo de sesión --------- */
        Text("Tiempo de sesión *", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            /* Cantidad (solo si procede) */
            AnimatedVisibility(form.sessionUnit != SessionUnit.INDEFINIDO) {
                InputText(
                    value          = form.sessionQty?.toString().orEmpty(),
                    onValueChange  = { v ->
                        form = form.copy(sessionQty = v.toIntOrNull())
                    },
                    placeholder    = "Cantidad",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier       = Modifier.weight(1f)
                )
            }

            /* Unidad */
            InputSelect(
                options          = listOf("Indefinido", "Minutos", "Horas"),
                selectedOption   = when (form.sessionUnit) {
                    SessionUnit.INDEFINIDO -> "Indefinido"
                    SessionUnit.MINUTOS    -> "Minutos"
                    SessionUnit.HORAS      -> "Horas"
                },
                onOptionSelected = { sel ->
                    val unit = when (sel) {
                        "Minutos"    -> SessionUnit.MINUTOS
                        "Horas"      -> SessionUnit.HORAS
                        else         -> SessionUnit.INDEFINIDO
                    }
                    form = form.copy(
                        sessionUnit = unit,
                        sessionQty  = form.sessionQty.takeIf { unit != SessionUnit.INDEFINIDO }
                    )
                },
                modifier         = Modifier.weight(1f)
            )
        }

        /* --------- Repetir hábito --------- */
        InputSelect(
            label            = "Repetir hábito *",
            options          = listOf(
                "Indefinido", "Diariamente", "Cada 3 días",
                "Semanalmente", "Cada 15 días", "Mensualmente"
            ),
            selectedOption   = when (form.repeatPattern) {
                RepeatPattern.INDEFINIDO     -> "Indefinido"
                RepeatPattern.DIARIO         -> "Diariamente"
                RepeatPattern.CADA_3_DIAS    -> "Cada 3 días"
                RepeatPattern.SEMANALMENTE   -> "Semanalmente"
                RepeatPattern.CADA_15_DIAS   -> "Cada 15 días"
                RepeatPattern.MENSUALMENTE   -> "Mensualmente"
            },
            onOptionSelected = { sel ->
                form = form.copy(repeatPattern = when (sel) {
                    "Diariamente"   -> RepeatPattern.DIARIO
                    "Cada 3 días"   -> RepeatPattern.CADA_3_DIAS
                    "Semanalmente"  -> RepeatPattern.SEMANALMENTE
                    "Cada 15 días"  -> RepeatPattern.CADA_15_DIAS
                    "Mensualmente"  -> RepeatPattern.MENSUALMENTE
                    else            -> RepeatPattern.INDEFINIDO
                })
            }
        )

        /* --------- Periodo total del hábito --------- */
        Text("Periodo del hábito *", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AnimatedVisibility(form.periodUnit != PeriodUnit.INDEFINIDO) {
                InputText(
                    value          = form.periodQty?.toString().orEmpty(),
                    onValueChange  = { v ->
                        form = form.copy(periodQty = v.toIntOrNull())
                    },
                    placeholder    = "Cantidad",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier       = Modifier.weight(1f)
                )
            }

            InputSelect(
                options          = listOf("Indefinido", "Días", "Semanas", "Meses"),
                selectedOption   = when (form.periodUnit) {
                    PeriodUnit.INDEFINIDO -> "Indefinido"
                    PeriodUnit.DIAS       -> "Días"
                    PeriodUnit.SEMANAS    -> "Semanas"
                    PeriodUnit.MESES      -> "Meses"
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
                modifier         = Modifier.weight(1f)
            )
        }

        /* --------- Switch de notificación --------- */
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
