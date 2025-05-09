// src/main/java/com/app/tibibalance/ui/screens/profile/ViewProfileScreen.kt
package com.app.tibibalance.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title

@Composable
fun ViewProfileScreen(viewModel: ViewProfileViewModel = hiltViewModel()) {
    val profileState by viewModel.profileState.collectAsState()
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    var isEditing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            FormContainer {
                ProfileContainer(
                    imageResId = R.drawable.imagenprueba,
                    size = 110.dp,
                    contentDescription = "Foto de perfil"
                )
                Spacer(Modifier.height(10.dp))

                // Nombre del usuario
                Title(text = profileState.name)

                Spacer(Modifier.height(20.dp))

                // Fecha de nacimiento
                Subtitle(text = "Fecha de nacimiento:")
                InputText(
                    value = profileState.birthDate,
                    onValueChange = { /*if (isEditing) viewModel.updateBirthDate(it)*/ },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Spacer(Modifier.height(10.dp))

                // Correo electrónico
                Subtitle(text = "Correo electrónico:")
                InputText(
                    value = profileState.email,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Spacer(Modifier.height(5.dp))

                // Botones de acción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryButton(
                        text = if (isEditing) "Guardar" else "Editar perfil",
                        onClick = { isEditing = !isEditing }, // Cambiar entre editar y guardar
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    ImageContainer(
                        resId = R.drawable.tibioprofileimage,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
