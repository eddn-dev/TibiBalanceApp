/* ui/wizard/step/SuggestionStep.kt */
package com.app.tibibalance.ui.wizard.step

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.HabitTemplate
import com.app.tibibalance.ui.components.*

@Composable
fun SuggestionStep(
    templates   : List<HabitTemplate>,
    onSuggestion: (HabitTemplate) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Title(
            "Hábitos sugeridos",
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        templates
            .groupBy { it.category }
            .forEach { (cat, list) ->
                Subtitle(cat.replaceFirstChar(Char::uppercase))
                list.forEach { tpl ->
                    /* ⬇️  ajustamos el callback */
                    SuggestionRow(tpl) { onSuggestion(tpl) }
                }
            }

    }
        // 🚫 Sin botones aquí: la acción global va en la barra inferior
}


/* ——— Fila reutilizable ——— */
@Composable
private fun SuggestionRow(
    tpl  : HabitTemplate,
    onAdd: () -> Unit
) = SettingItem(
    leadingIcon = {
        ImageContainer(
            resId              = mapIcon(tpl.icon),
            contentDescription = tpl.name
        )
    },
    text     = tpl.name,
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

/* Mapea nombre Material Icon → drawable local */
private fun mapIcon(material: String) = when (material) {
    "LocalDrink"       -> R.drawable.iconwaterimage
    "Book"             -> R.drawable.iconbookimage
    "Bedtime"          -> R.drawable.iconsleepimage
    "WbSunny"          -> R.drawable.iconsunimage
    "SelfImprovement"  -> R.drawable.iconmeditationimage
    else               -> R.drawable.iconwaterimage
}
