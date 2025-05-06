package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.screens.habits.HabitUi

@Composable
internal fun HabitList(
    habits : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit,
    onAdd  : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeaderBox("Mis hábitos")

        Category("Salud", habits.filter { it.icon == "LocalDrink" || it.icon == "Bedtime" },
            onCheck, onEdit)
        Category("Productividad", habits.filter { it.icon == "MenuBook" }, onCheck, onEdit)
        Category("Bienestar", habits.filter { it.icon == "SelfImprovement" }, onCheck, onEdit)

        Box(Modifier.fillMaxWidth(), Alignment.Center) {
            RoundedIconButton(
                onClick = onAdd,
                icon = Icons.Default.Add,
                contentDescription = "Agregar hábito",
                backgroundColor = Color(0xFF3EA8FE),
                iconTint = Color.White
            )
        }
    }
}

@Composable
private fun Category(
    title : String,
    rows  : List<HabitUi>,
    onCheck: (HabitUi, Boolean) -> Unit,
    onEdit : (HabitUi) -> Unit
) {
    if (rows.isEmpty()) return
    Subtitle(title)
    rows.forEach { h ->
        SettingItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.run {
                        when (h.icon) {
                            "LocalDrink"       -> LocalDrink
                            "Bedtime"          -> Bedtime
                            "MenuBook"         -> Icons.AutoMirrored.Filled.MenuBook
                            "SelfImprovement"  -> SelfImprovement
                            else               -> CheckBoxOutlineBlank
                        }
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            text = h.name,
            trailing = {
                Checkbox(
                    checked = h.checked,
                    onCheckedChange = { onCheck(h, it) }
                )
            },
            onClick = { onEdit(h) }
        )
    }
}

@Composable
internal fun EmptyState(onAdd: () -> Unit) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(24.dp)
) {
    /* Flecha que “apunta” al botón; usa iconos Material 3 */
    Icon(
        imageVector       = Icons.Filled.ArrowDownward,   // disponible en compose-material :contentReference[oaicite:2]{index=2}
        contentDescription = null,
        tint              = MaterialTheme.colorScheme.primary,
        modifier          = Modifier.size(96.dp)
    )

    Description(
        text = "¡Aún no tienes hábitos!\nPulsa el + para crear tu primer hábito.",
        textAlign = TextAlign.Center
    )

    RoundedIconButton(
        onClick            = onAdd,
        icon               = Icons.Default.Add,
        contentDescription = "Agregar hábito",
        backgroundColor    = Color(0xFF3EA8FE),
        iconTint           = Color.White
    )
}


@Composable
private fun HeaderBox(text: String) = Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF85C3DE), RoundedCornerShape(15.dp))
        .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center
) {
    Title(text, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
}

@Composable
internal fun Centered(txt: String) = Box(
    Modifier.fillMaxSize(), Alignment.Center
) { androidx.compose.material3.Text(txt, fontSize = 32.sp) }
