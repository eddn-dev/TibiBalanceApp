package com.app.tibibalance.ui.screens.profile

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.MaterialTheme
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
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.screens.settings.SettingsUiState
import com.app.tibibalance.ui.screens.settings.SettingsViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // 1. Estado de la UI
    val uiState by viewModel.ui.collectAsState()
    val profile = (uiState as? SettingsUiState.Ready)?.profile

    // 2. Campos editables
    var username  by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var photoUrl  by remember { mutableStateOf<String?>(null) }

    // 3. Email fijo desde Firebase Auth
    val currentUser = Firebase.auth.currentUser
    val email = currentUser?.email.orEmpty()

    // 4. Inicializar valores al cargar el perfil
    LaunchedEffect(profile) {
        profile?.let {
            username  = it.userName.orEmpty()
            birthDate = it.birthDate.orEmpty()
            photoUrl  = it.photoUrl
        }
    }

    // 5. Configuración de DatePicker
    val context = LocalContext.current
    val (day0, month0, year0) = birthDate
        .split("/")
        .takeIf { it.size == 3 }
        ?.let { (d, m, y) -> Triple(d.toInt(), m.toInt() - 1, y.toInt()) }
        ?: Triple(1, 0, 2000)
    val datePicker = remember {
        DatePickerDialog(context, { _, yy, mm, dd ->
            birthDate = "%02d/%02d/%04d".format(dd, mm + 1, yy)
        }, year0, month0, day0)
    }

    // 6. Lógica de guardado
    val scope = rememberCoroutineScope()
    fun onSave() {
        scope.launch {
            viewModel.updateProfile(username, birthDate)
            Firebase.auth.currentUser
                ?.updateProfile(userProfileChangeRequest { displayName = username })
                ?.await()
            navController.popBackStack()
        }
    }

    // 7. UI
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFC3E2FA), Color.White)))
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
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                // Correo electrónico (solo lectura)
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Correo electrónico", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = email,
                    onValueChange = { /* no editable */ },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Fecha de nacimiento
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Fecha de nacimiento", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { /* no editable */ },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { datePicker.show() }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Contraseña (navega a cambiar)
                Spacer(Modifier.height(10.dp))
                Subtitle(text = "Contraseña", modifier = Modifier.align(Alignment.Start))
                OutlinedTextField(
                    value = "••••••••",
                    onValueChange = { /* no editable */ },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("changePassword") },
                    trailingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Cambiar contraseña")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                // Botones de acción
                Spacer(Modifier.height(20.dp))
                Row(
                    Modifier.fillMaxWidth(),
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
