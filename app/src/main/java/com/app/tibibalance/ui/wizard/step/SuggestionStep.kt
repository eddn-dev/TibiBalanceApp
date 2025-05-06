/* ui/wizard/step/SuggestionStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.inputs.iconByName
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title

@Composable
fun SuggestionStep(
    templates   : List<HabitTemplate>,
    onSuggestion: (HabitTemplate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 20.dp)      // 👈  un poco más de aire
    ) {
        Title(
            text      = "Hábitos sugeridos",
            modifier  = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),                       // separación título-lista
            textAlign = TextAlign.Center
        )

        /* Agrupamos por el enum → usamos la propiedad `display` */
        templates
            .groupBy { it.category }                            // key = HabitCategory
            .forEach { (cat, list) ->
                Subtitle(
                    text     = cat.display,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp) // espacio entre filas
                ) {
                    list.forEach { tpl ->
                        SuggestionRow(tpl) { onSuggestion(tpl) }
                    }
                }
                Spacer(Modifier.height(8.dp))                   // aire entre categorías
            }
    }
}

/* ——— Fila reutilizable ——— */
@Composable
private fun SuggestionRow(
    tpl  : HabitTemplate,
    onAdd: () -> Unit
) = SettingItem(
    leadingIcon = {
        Icon(
            painter            = rememberVectorPainter(iconByName(tpl.icon)),
            contentDescription = tpl.name,
            modifier           = Modifier.size(32.dp)
        )
    },
    text = tpl.name,
    trailing = {
        RoundedIconButton(
            onClick            = onAdd,
            icon               = Icons.Default.Add,
            contentDescription = "Agregar hábito",
            modifier           = Modifier.size(32.dp),
            backgroundColor    = Color(0xFF3EA8FE)
        )
    },
    onClick = onAdd
)
