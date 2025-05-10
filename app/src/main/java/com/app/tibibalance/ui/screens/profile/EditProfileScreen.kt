package com.app.tibibalance.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.screens.settings.SettingsUiState
import com.app.tibibalance.ui.screens.settings.SettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // Observa el estado del perfil desde SettingsViewModel
    val uiState   by viewModel.ui.collectAsState()
    val profile   = (uiState as? SettingsUiState.Ready)?.profile

    // Estados locales para los campos
    var username  by remember { mutableStateOf("") }
    var email     by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var photoUrl  by remember { mutableStateOf<String?>(null) }

    // Inicializar valores cuando llega el perfil
    LaunchedEffect(profile) {
        profile?.let {
            username  = it.userName.orEmpty()
            email     = it.email.orEmpty()
            birthDate = it.birthDate.orEmpty()
            photoUrl  = it.photoUrl
        }
    }

    // Configuración del DatePicker a partir de birthDate (dd/MM/yyyy)
    val context = LocalContext.current
    val (d0, m0, y0) = birthDate
        .split("/")
        .takeIf { it.size == 3 }
        ?.let { (d, m, y) ->
            Triple(d.toIntOrNull() ?: 1, (m.toIntOrNull() ?: 1) - 1, y.toIntOrNull() ?: 2000)
        } ?: Triple(1, 0, 2000)
    val datePicker = remember {
        DatePickerDialog(context, { _, year, month, day ->
            birthDate = "%02d/%02d/%04d".format(day, month + 1, year)
        }, y0, m0, d0)
    }

    // Scope para acciones de guardado
    val scope = rememberCoroutineScope()
    fun onSave() {
        scope.launch {
            // Actualiza nombre y fecha en ProfileRepository vía ViewModel
            viewModel.updateProfile(username, birthDate)
            // Sincroniza displayName en Firebase Auth
            Firebase.auth.currentUser
                ?.updateProfile(userProfileChangeRequest { displayName = username })
                ?.await()
            // Regresa a la pantalla anterior
            navController.popBackStack()
        }
    }

    // UI original inyectada directamente aquí
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(Color(0xFFC3E2FA), Color.White)
                )
            )
    ) {
        item {
            Header("Editar información personal")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUrl ?: R.drawable.imagenprueba)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { /* TODO: cambiar foto */ }
                )
                Spacer(Modifier.height(5.dp))
                SecondaryButton(
                    text = "CAMBIAR FOTO",
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .width(170.dp)
                        .height(38.dp)
                )

                // Nombre de usuario
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Nombre de usuario", modifier = Modifier.align(Alignment.Start))
                InputText(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth()
                )

                // Correo (sólo lectura)
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Correo electrónico", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = email,
                    onValueChange = { /* read-only */ },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // --------------------------------------
// Fecha de nacimiento (dentro del Column)
// --------------------------------------
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Fecha de nacimiento", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { /* read-only */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    trailingIcon = {
                        // envuelve en IconButton para captar el clic
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Seleccionar fecha"
                            )
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )


                // Contraseña
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Contraseña", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = "••••••••",
                    onValueChange = { /* read-only */ },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("changePassword") },
                    trailingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Botones Guardar / Cancelar
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecondaryButton(
                        text = "Guardar",
                        onClick = { onSave() },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Cancelar",
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp)
                    )
                }
            }
        }
    }
}
