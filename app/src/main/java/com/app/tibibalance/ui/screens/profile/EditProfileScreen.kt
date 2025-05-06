package com.app.tibibalance.ui.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*

@Composable
fun EditProfileScreen(
    onChangePhoto: () -> Unit = {},
    onSave: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    // Estado de la fecha
    var date by remember { mutableStateOf("01/05/2025") }
    val context = LocalContext.current

    // DatePickerDialog externo
    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                date = "%02d/%02d/%04d".format(day, month + 1, year)
            },
            2025, 4, 1
        )
    }

    // Fondo degradado
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(1.dp))

            ProfileContainer(
                imageResId = R.drawable.imagenprueba,
                size = 110.dp,
                contentDescription = "Foto de perfil"
            )

            Spacer(Modifier.height(5.dp))

            SecondaryButton(
                text = "Cambiar Foto",
                onClick = onChangePhoto,
                modifier = Modifier
                    .width(150.dp)
                    .height(40.dp)
            )

            Spacer(Modifier.height(6.dp))

            Subtitle(text = "Nombre de usuario:")
            InputText(
                value = "nora soto",
                onValueChange = { /* TODO */ },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(2.dp))

            Subtitle(text = "Correo electrónico:")
            InputText(
                value           = "norasoto5@gmail.com",
                onValueChange   = { /* no editable */ },
                modifier        = Modifier
                    .fillMaxWidth()
                    .padding(bottom = (-4).dp),   // si aún quieres un “tirón” hacia arriba
                supportingText  = "ℹ\uFE0F El correo electrónico no es editable"
            )

            Spacer(Modifier.height(5.dp))

            Subtitle(text = "Fecha de nacimiento:")
            // ─── reemplazamos aquí InputDate ─────────────────────────────
            OutlinedTextField(
                value = date,
                onValueChange = { /* read-only */ },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePicker.show() },
                label = { Text("Fecha de nacimiento") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleccionar fecha",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    // textColors: usa onSurface cuando esté disabled
                    focusedTextColor       = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor     = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor      = MaterialTheme.colorScheme.onSurface,
                    errorTextColor         = MaterialTheme.colorScheme.error,
                    // containerColors
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor  = MaterialTheme.colorScheme.surface,
                    errorContainerColor     = MaterialTheme.colorScheme.surface,
                    // borderColors
                    focusedBorderColor      = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                    disabledBorderColor     = MaterialTheme.colorScheme.outline,
                    errorBorderColor        = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(5.dp))

            Subtitle(text = "Contraseña:")
            InputText(
                value = "********",
                onValueChange = { /* TODO */ },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SecondaryButton(
                    text = "Guardar",
                    onClick = onSave,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = "Cancelar",
                    onClick = onCancel,
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewFeatureEditProfileScreen() {
    EditProfileScreen()
}
