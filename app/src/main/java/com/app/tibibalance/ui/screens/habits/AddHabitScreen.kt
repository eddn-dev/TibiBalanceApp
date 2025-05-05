// ui/screens/habits/AddHabitScreen.kt
package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.ImageContainer
import com.app.tibibalance.ui.components.ModalContainer
import com.app.tibibalance.ui.components.PrimaryButton
import com.app.tibibalance.ui.components.RoundedIconButton
import com.app.tibibalance.ui.components.Subtitle
import com.app.tibibalance.ui.components.Title

@Composable
fun AddHabitScreen(
    onDismissRequest: () -> Unit
) {
    ModalContainer(
        onDismissRequest = onDismissRequest,
        containerColor =   Color(0xFFAED3E3)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFAED3E3).copy(alpha = 0.45f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Pestaña activa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color((0xFF85C3DE)), shape = RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Title(
                    text  = "Hábitos Sugeridos",
                    modifier  = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            // Salud
            Subtitle(text = "Salud")
            SuggestionRow(R.drawable.iconwaterimage, "Beber 2 litros de agua al día") { /*…*/ }
            SuggestionRow(R.drawable.iconsleepimage, "Dormir mínimo \n 7 horas")      { /*…*/ }

            // Productividad
            Subtitle(text = "Productividad")
            SuggestionRow(R.drawable.iconbookimage, "Leer 20 páginas de un libro")    { /*…*/ }
            SuggestionRow(R.drawable.iconsunimage, "Tender la cama cada mañana")      { /*…*/ }

            // Bienestar
            Subtitle(text = "Bienestar")
            SuggestionRow(R.drawable.iconmeditationimage, "Meditar 5 minutos al despertar") { /*…*/ }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                text      = "Crear mi propio hábito",
                onClick   = { /*…*/ },
                container = Color.White,
                contentColor = Color.Black,
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        }
    }
}

@Composable
private fun SuggestionRow(
    iconRes: Int,
    label: String,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageContainer(
            resId              = iconRes,
            contentDescription = label,
            modifier           = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        RoundedIconButton(
            onClick           = onAdd,
            icon              = Icons.Default.Add,
            contentDescription = "Agregar hábito",
            modifier          = Modifier.size(32.dp),
            backgroundColor   = Color(0xFF3EA8FE),
            iconTint          = Color.White
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewAddHabitScreen() {
    AddHabitScreen(onDismissRequest = {})
}
