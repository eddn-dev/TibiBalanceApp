//EditProfileScreen.kt
package com.app.tibibalance.ui.screens.profile

import android.Manifest
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados editables
    var username by rememberSaveable { mutableStateOf("") }
    var originalUsername by rememberSaveable { mutableStateOf("") }
    var birthDate by rememberSaveable { mutableStateOf("") }
    var originalBirthDate by rememberSaveable { mutableStateOf("") }

    // Preview de foto y URI pendiente
    var photoUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingPhotoUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val email = Firebase.auth.currentUser?.email.orEmpty()

    // 1) Launcher para abrir galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingPhotoUri = it
            photoUrl = it.toString()  // preview inmediato
        }
    }

    // 2) Launcher para pedir permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función helper
    fun openGallery() {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
        permissionLauncher.launch(perm)
    }

    // Carga inicial de datos
    LaunchedEffect(Unit) {
        viewModel.loadInitialProfile()?.let { profile ->
            username = profile.userName.orEmpty().also { originalUsername = it }
            birthDate = profile.birthDate.orEmpty().also { originalBirthDate = it }
            photoUrl = profile.photoUrl
        }
    }

    // Configuración del DatePicker
    val (d0, m0, y0) = birthDate.split("/").takeIf { it.size == 3 }
        ?.let { (d, m, y) -> Triple(d.toInt(), m.toInt() - 1, y.toInt()) }
        ?: Triple(1, 0, Calendar.getInstance().get(Calendar.YEAR) - 18)

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, y, m, d -> birthDate = "%02d/%02d/%04d".format(d, m + 1, y) },
            y0, m0, d0
        ).apply {
            val maxDate = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
            datePicker.maxDate = maxDate.timeInMillis
        }
    }

    // Validaciones
    val todayMs = Calendar.getInstance().timeInMillis
    val cal18 = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
    val selMs = runCatching {
        birthDate.split("/").let { (d, m, y) ->
            Calendar.getInstance().apply {
                set(y.toInt(), m.toInt() - 1, d.toInt())
            }.timeInMillis
        }
    }.getOrNull() ?: 0L

    val dateChanged = birthDate != originalBirthDate
    val dateValid = !dateChanged || (birthDate.isNotBlank() && selMs <= cal18.timeInMillis && selMs <= todayMs)
    val hasTextChanges = username != originalUsername || dateChanged
    val hasChanges = hasTextChanges || pendingPhotoUri != null

    // Al pulsar "Guardar"
    fun onSave() {
        scope.launch {
            // 1) actualizar nombre/fecha
            viewModel.updateProfile(
                name = username.takeIf { it.isNotBlank() },
                birthDate = birthDate.takeIf { dateChanged }
            )
            // 2) subir foto si se seleccionó una
            pendingPhotoUri?.let { viewModel.updateProfilePhoto(it, context) }
            // 3) actualizar displayName en FirebaseAuth
            Firebase.auth.currentUser
                ?.updateProfile(userProfileChangeRequest { displayName = username })
                ?.await()
            // 4) volver atrás
            navController.popBackStack()
        }
    }

    // UI
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
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
                    // Avatar con clic para abrir galería
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
                            .clickable { openGallery() }
                    )
                    Spacer(Modifier.height(10.dp))

                    // Botón alternativo (opcional)
                    SecondaryButton(
                        text = "CAMBIAR FOTO",
                        onClick = { openGallery() },
                        modifier = Modifier
                            .width(170.dp)
                            .height(38.dp)
                    )

                    Spacer(Modifier.height(20.dp))
                    Subtitle("Nombre de usuario", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(10.dp))
                    Subtitle("Correo electrónico", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { /* no hacemos nada porque es readonly */ },
                        readOnly = true,
                        enabled = false,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor    = Color.White,
                            unfocusedContainerColor  = Color.White,
                            disabledContainerColor   = Color.White,
                            disabledBorderColor      = MaterialTheme.colorScheme.outline
                        ),
                        supportingText = {
                            Text(
                                "El correo electrónico no es editable",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )


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
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Text(
                        text = "Debes tener al menos 18 años",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dateValid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )

                    Spacer(Modifier.height(10.dp))
                    Subtitle("Contraseña", Modifier.align(Alignment.Start))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable {
                                navController.navigate("changePassword")
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "••••••••",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Cambiar contraseña",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SecondaryButton(
                            text = "Guardar",
                            onClick = { onSave() },
                            enabled = hasChanges && dateValid,
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = "Cancelar",
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.width(150.dp).height(40.dp)
                        )
                    }
                }
            }
        }

        // Mostrar errores
        state.error?.let { errorMsg ->
            LaunchedEffect(errorMsg) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.consumeError()
            }
        }
    }
}