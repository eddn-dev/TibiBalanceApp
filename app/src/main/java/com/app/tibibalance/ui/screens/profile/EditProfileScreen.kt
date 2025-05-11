// EditProfileScreen.kt
package com.app.tibibalance.ui.screens.profile

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.saveable.rememberSaveable           // <‑‑ FALTABA
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
import com.app.tibibalance.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    /* ---------- 1. UI STATE ---------- */
    val uiState by viewModel.ui.collectAsState()
    val profile = (uiState as? SettingsUiState.Ready)?.profile

    /* ---------- 2. Launchers ---------- */
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.uploadProfilePhoto(it) } }

    /* ---------- 3. Campos editables ---------- */
    var username  by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var photoUrl  by rememberSaveable { mutableStateOf<String?>(null) }

    /* ---------- 4. Email (read‑only) ---------- */
    val email = Firebase.auth.currentUser?.email.orEmpty()

    /* ---------- 5. Inicializar con datos del perfil ---------- */
    LaunchedEffect(profile) {
        profile?.let {
            username  = it.userName.orEmpty()
            birthDate = it.birthDate.orEmpty()
            photoUrl  = it.photoUrl
        }
    }

    /* ---------- 6. DatePicker ---------- */
    val context = LocalContext.current
    val (d0, m0, y0) = birthDate.split("/").takeIf { it.size == 3 }
        ?.let { (d, m, y) -> Triple(d.toInt(), m.toInt() - 1, y.toInt()) }
        ?: Triple(1, 0, 2000)
    val datePicker = remember {
        DatePickerDialog(context, { _, y, m, d ->
            birthDate = "%02d/%02d/%04d".format(d, m + 1, y)
        }, y0, m0, d0)
    }

    /* ---------- 7. Guardar ---------- */
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

    /* ---------- 8. UI ---------- */
    Box(modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFC3E2FA), Color.White))
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
                    /* Foto ------------------------------------------------ */
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
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )
                    Spacer(Modifier.height(5.dp))
                    SecondaryButton(
                        text = "CAMBIAR FOTO",
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .width(170.dp)
                            .height(38.dp)
                    )

                    /* Nombre --------------------------------------------- */
                    Spacer(Modifier.height(10.dp))
                    Subtitle("Nombre de usuario", Modifier.align(Alignment.Start))
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

                    /* Email ---------------------------------------------- */
                    Spacer(Modifier.height(10.dp))
                    Subtitle("Correo electrónico", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {},                    // sin parámetro it
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

                    /* Fecha ---------------------------------------------- */
                    Spacer(Modifier.height(10.dp))
                    Subtitle("Fecha de nacimiento", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.CalendarToday, null)
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

                    /* Contraseña ----------------------------------------- */
                    Spacer(Modifier.height(10.dp))
                    Subtitle("Contraseña", Modifier.align(Alignment.Start))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.ChangePassword.route) } // navega
                    ) {
                        OutlinedTextField(
                            value = "••••••••",
                            onValueChange = { /* no editable */ },
                            readOnly = true,
                            enabled  = false,                     // deshabilitado ⇒ no consume click
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                // Contenedor (normal y disabled idénticos)
                                focusedContainerColor    = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor  = MaterialTheme.colorScheme.surface,
                                disabledContainerColor   = MaterialTheme.colorScheme.surface,

                                // Borde (normal y disabled)
                                focusedBorderColor       = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor     = MaterialTheme.colorScheme.outline,
                                disabledBorderColor      = MaterialTheme.colorScheme.outline,

                                // Texto (normal y disabled)
                                disabledTextColor        = MaterialTheme.colorScheme.onSurface,
                                focusedTextColor         = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor       = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }



                    /* Botones ------------------------------------------- */
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

        /* ---------- Loader y errores ---------- */
        when (uiState) {
            SettingsUiState.Loading -> Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = .2f)),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is SettingsUiState.Error -> {
                val msg = (uiState as SettingsUiState.Error).message
                LaunchedEffect(msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.consumeError()
                }
            }

            else -> Unit
        }
    }
}
