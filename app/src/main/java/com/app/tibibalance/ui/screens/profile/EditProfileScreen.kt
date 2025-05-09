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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.Header
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.inputs.InputText
import com.app.tibibalance.ui.components.texts.Subtitle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase             // ← IMPORT CORRECTO
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.LaunchedEffect

// ——————————————————————————————————————————
// 1) La UI pura, sin lógica, recibe todos los datos y callbacks
// ——————————————————————————————————————————
@Composable
fun EditProfileContent(
    photoUrl: String?,
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    birthDate: String,
    onBirthDateClick: () -> Unit,
    passwordHidden: Boolean,
    onPasswordClick: () -> Unit,
    onChangePhotoClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFC3E2FA), Color.White)))
    ) {
        item {
            Header("Editar información personal")

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(photoUrl ?: R.drawable.imagenprueba)
                        .build(),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onChangePhotoClick() }
                )
                Spacer(Modifier.height(10.dp))

                SecondaryButton(
                    text = "CAMBIAR FOTO",
                    onClick = onChangePhotoClick,
                    modifier = Modifier
                        .width(170.dp)
                        .height(38.dp)
                )
                Spacer(Modifier.height(10.dp))

                Subtitle("Nombre de usuario", Modifier.align(Alignment.Start))
                InputText(
                    value = username,
                    onValueChange = onUsernameChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                Subtitle("Correo electrónico", Modifier.align(Alignment.Start))
                InputText(
                    value = email,
                    onValueChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                Subtitle("Fecha de nacimiento", Modifier.align(Alignment.Start))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = onBirthDateClick) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor      = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor    = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(10.dp))

                Subtitle("Contraseña", Modifier.align(Alignment.Start))
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = if (passwordHidden) "••••••••" else "",
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPasswordClick() },
                    trailingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor      = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor    = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(20.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SecondaryButton(
                        text = "Guardar",
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Cancelar",
                        onClick = onCancelClick,
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp)
                    )
                }
            }
        }
    }
}

// ——————————————————————————————————————————
    // 2) El wrapper que el NavGraph llamará: monta estado y datePicker
// ——————————————————————————————————————————
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // — Wrapper: lógica de carga de datos y estado —
    val user = Firebase.auth.currentUser
    val db   = Firebase.firestore
    val uid  = user?.uid

    var username by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var birth    by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf(user?.photoUrl?.toString()) }

    LaunchedEffect(uid) {
        if (uid != null) {
            try {
                val snap = db.collection("users")
                    .document(uid)
                    .get()
                    .await()
                username = snap.getString("userName").orEmpty()
                email    = snap.getString("email").orEmpty()
                birth    = snap.getString("birthDate").orEmpty()
                snap.getString("photoUrl")?.let { photoUrl = it }
            } catch (_: Exception) {
                username = user?.email?.substringBefore("@").orEmpty()
                email    = user?.email.orEmpty()
            }
        }
    }

    // Cuando quieras llamar a tu UI pura, pásale estos estados:
    EditProfileContent(
        photoUrl           = photoUrl,
        username           = username,
        onUsernameChange   = { username = it },
        email              = email,
        onEmailChange      = { /* opcional */ },
        birthDate          = birth,
        onBirthDateClick   = { /* despliega DatePicker */ },
        passwordHidden     = true,
        onPasswordClick    = { /* … */ },
        onChangePhotoClick = { /* … */ },
        onSaveClick        = { /* guarda de nuevo en Firestore */ },
        onCancelClick      = { navController.popBackStack() },
        modifier           = modifier
    )
}
