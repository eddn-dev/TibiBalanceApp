package com.app.tibibalance.ui.wizard.step

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.*
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.inputs.*
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.wizard.HabitFormSaver

@OptIn(ExperimentalFoundationApi::class)        // FlowRow aún es experimental
@Composable
fun TrackingStep(
    initial      : HabitForm,
    errors       : List<String>,
    onFormChange : (HabitForm) -> Unit,
    onBack       : () -> Unit = {}
) {
    /* -------- estado local serializable -------- */
    var form   by rememberSaveable(stateSaver = HabitFormSaver) { mutableStateOf(initial) }
    var infoDlg by remember { mutableStateOf(false) }
    LaunchedEffect(form) { onFormChange(form) }

    /* Apaga modo reto si sus requisitos dejan de cumplirse */
    LaunchedEffect(form.repeatPreset, form.periodUnit) {
        if (form.challenge &&
            (form.repeatPreset == RepeatPreset.INDEFINIDO ||
                    form.periodUnit   == PeriodUnit.INDEFINIDO)
        ) form = form.copy(challenge = false)
    }

    /* -------- flags de error -------- */
    val sessionQtyErr = errors.any { it.contains("duración", true) }
    val periodQtyErr  = errors.any { it.contains("periodo",  true) }
    val weekDaysErr   = errors.any { it.contains("día",      true) }
    val repeatErr     = errors.any { it.contains("repetición", true) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Title("Parámetros de seguimiento", Modifier.fillMaxWidth())

        /* ---------- Duración de la sesión ---------- */
        Text("Duración de la actividad", style = MaterialTheme.typography.bodyMedium)
        Row(
            Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(form.sessionUnit != SessionUnit.INDEFINIDO) {
                InputNumber(
                    value          = form.sessionQty?.toString().orEmpty(),
                    onValueChange  = { form = form.copy(sessionQty = it.toIntOrNull()) },
                    placeholder    = "Cantidad",
                    modifier       = Modifier.width(120.dp),
                    isError        = sessionQtyErr,
                    supportingText = if (sessionQtyErr) "Requerido" else null
                )
            }

            InputSelect(
                options         = listOf("Indefinido","Minutos","Horas"),
                selectedOption  = when (form.sessionUnit) {
                    SessionUnit.MINUTOS -> "Minutos"
                    SessionUnit.HORAS   -> "Horas"
                    else                -> "Indefinido"
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

        /* ---------- Repetición ---------- */
        InputSelect(
            label            = "Repetir hábito",
            options          = listOf("Indefinido","Diariamente","Cada 3 días",
                "Semanal","Quincenal","Mensual","Personalizado"),
            selectedOption   = when (form.repeatPreset) {
                RepeatPreset.DIARIO       -> "Diariamente"
                RepeatPreset.CADA_3_DIAS   -> "Cada 3 días"
                RepeatPreset.SEMANAL       -> "Semanal"
                RepeatPreset.QUINCENAL     -> "Quincenal"
                RepeatPreset.MENSUAL       -> "Mensual"
                RepeatPreset.PERSONALIZADO -> "Personalizado"
                else                       -> "Indefinido"
            },
            onOptionSelected = { sel ->
                form = form.copy(
                    repeatPreset = when (sel) {
                        "Diariamente"   -> RepeatPreset.DIARIO
                        "Cada 3 días"   -> RepeatPreset.CADA_3_DIAS
                        "Semanal"       -> RepeatPreset.SEMANAL
                        "Quincenal"     -> RepeatPreset.QUINCENAL
                        "Mensual"       -> RepeatPreset.MENSUAL
                        "Personalizado" -> RepeatPreset.PERSONALIZADO
                        else            -> RepeatPreset.INDEFINIDO
                    },
                    weekDays = if (sel != "Personalizado") emptySet() else form.weekDays
                )
            },
            isError        = repeatErr,
            supportingText = if (repeatErr) "Requerido para modo reto" else null,
            modifier       = Modifier.padding(bottom = 4.dp)      // menos espacio
        )

        /* ---------- Días de la semana (rejilla) ---------- */
        AnimatedVisibility(form.repeatPreset == RepeatPreset.PERSONALIZADO) {
            Column {
                Text("Días de la semana", style = MaterialTheme.typography.bodyMedium)
                FlowRow(                                        // grilla flexible
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement   = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val labels = listOf("L","M","X","J","V","S","D")
                    labels.forEachIndexed { idx, lbl ->
                        val day = idx + 1
                        val selected = day in form.weekDays
                        FilterChip(
                            selected = selected,
                            onClick  = {
                                form = if (selected)
                                    form.copy(weekDays = form.weekDays - day)
                                else
                                    form.copy(weekDays = form.weekDays + day)
                            },
                            label    = { Text(lbl) }
                        )
                    }
                }
                if (weekDaysErr)
                    Text("Selecciona al menos un día",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error))
            }
        }

        /* ---------- Periodo total ---------- */
        Text("Periodo del hábito", style = MaterialTheme.typography.bodyMedium)
        Row(
            Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(form.periodUnit != PeriodUnit.INDEFINIDO) {
                InputNumber(
                    value          = form.periodQty?.toString().orEmpty(),
                    onValueChange  = { form = form.copy(periodQty = it.toIntOrNull()) },
                    placeholder    = "Cantidad",
                    modifier       = Modifier.width(120.dp),
                    isError        = periodQtyErr,
                    supportingText = if (periodQtyErr) "Requerido" else null
                )
            }

            InputSelect(
                options         = listOf("Indefinido","Días","Semanas","Meses"),
                selectedOption  = when (form.periodUnit) {
                    PeriodUnit.DIAS    -> "Días"
                    PeriodUnit.SEMANAS -> "Semanas"
                    PeriodUnit.MESES   -> "Meses"
                    else               -> "Indefinido"
                },
                onOptionSelected = { sel ->
                    val unit = when (sel) {
                        "Días"    -> PeriodUnit.DIAS
                        "Semanas" -> PeriodUnit.SEMANAS
                        "Meses"   -> PeriodUnit.MESES
                        else      -> PeriodUnit.INDEFINIDO
                    }
                    form = form.copy(
                        periodUnit = unit,
                        periodQty  = form.periodQty.takeIf { unit != PeriodUnit.INDEFINIDO }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError  = periodQtyErr && form.periodUnit != PeriodUnit.INDEFINIDO
            )
        }

        /* ---------- Toggles ---------- */
        if (form.repeatPreset != RepeatPreset.INDEFINIDO) {
            ToggleRow(
                label    = "Notificarme",
                checked  = form.notify,
                onToggle = { form = form.copy(notify = !form.notify) }
            )
        }
        ToggleRow(
            label   = "Modo reto",
            checked = form.challenge,
            onToggle = {
                val canEnable = form.repeatPreset != RepeatPreset.INDEFINIDO &&
                        form.periodUnit   != PeriodUnit.INDEFINIDO
                if (canEnable)
                    form = form.copy(challenge = !form.challenge)
                else
                    infoDlg = true
            },
            trailing = {
                IconButton(onClick = { infoDlg = true }) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            }
        )
    }

    /* ---------- diálogo ayuda reto ---------- */
    if (infoDlg) {
        ModalInfoDialog(
            visible  = true,
            icon     = Icons.Default.Info,
            title    = "Modo reto",
            message  = "Cuando un hábito está en modo reto no se puede editar.\n" +
                    "Debes definir repetición y periodo obligatorios.",
            primaryButton = DialogButton("Entendido") { infoDlg = false }
        )
    }
}

/* ---------- helper fila de switches ---------- */
@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit,
    trailing: @Composable () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            trailing()
            Spacer(Modifier.width(4.dp))
            SwitchToggle(checked = checked, onCheckedChange = { onToggle() })
        }
    }
}
