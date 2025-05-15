package com.app.tibibalance.ui.screens.profile

import android.Manifest
import androidx.compose.ui.draw.shadow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.app.tibibalance.ui.components.dialogs.DialogButton
import com.app.tibibalance.ui.components.dialogs.ModalInfoDialog
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
    val canChange by viewModel.canChangePassword.collectAsState()
    val state    by viewModel.state.collectAsState()
    val context  = LocalContext.current
    val scope    = rememberCoroutineScope()

    // Editable fields
    var username            by rememberSaveable { mutableStateOf("") }
    var originalUsername    by rememberSaveable { mutableStateOf("") }
    var birthDate           by rememberSaveable { mutableStateOf("") }
    var originalBirthDate   by rememberSaveable { mutableStateOf("") }
    var photoUrl            by rememberSaveable { mutableStateOf<String?>(null) }
    var pendingPhotoUri     by rememberSaveable { mutableStateOf<Uri?>(null) }

    // Modal success
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Image picker + permission
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { pendingPhotoUri = it; photoUrl = it.toString() }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) imagePickerLauncher.launch("image/*")
    }
    fun openGallery() {
        val perm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else Manifest.permission.READ_EXTERNAL_STORAGE
        permissionLauncher.launch(perm)
    }

    // Load initial
    LaunchedEffect(Unit) {
        viewModel.loadInitialProfile()?.let { profile ->
            username           = profile.userName.orEmpty().also { originalUsername = it }
            birthDate          = profile.birthDate.orEmpty().also { originalBirthDate = it }
            photoUrl           = profile.photoUrl
        }
    }

    // DatePicker setup
    val (d0, m0, y0) = birthDate.split("/").takeIf { it.size==3 }?.let{(d,m,y)->
        Triple(d.toInt(),m.toInt()-1,y.toInt())
    }?: Triple(1,0,Calendar.getInstance().get(Calendar.YEAR)-18)
    val datePicker = remember {
        DatePickerDialog(context,{_,yy,mm,dd->
            birthDate = "%02d/%02d/%04d".format(dd,mm+1,yy)
        },y0,m0,d0).apply{
            datePicker.maxDate = Calendar.getInstance().apply{ add(Calendar.YEAR,-18) }.timeInMillis
        }
    }

    // Validations
    val todayMs = Calendar.getInstance().timeInMillis
    val cal18   = Calendar.getInstance().apply{ add(Calendar.YEAR,-18) }.timeInMillis
    val selMs   = runCatching{
        birthDate.split("/").let{(d,m,y)->
            Calendar.getInstance().apply{ set(y.toInt(),m.toInt()-1,d.toInt()) }.timeInMillis
        }
    }.getOrNull()?:0L
    val dateChanged    = birthDate!=originalBirthDate
    val dateValid      = !dateChanged || (birthDate.isNotBlank() && selMs<=cal18 && selMs<=todayMs)
    val hasTextChanges = username!=originalUsername || dateChanged
    val hasChanges     = hasTextChanges || pendingPhotoUri!=null

    // Observe save success
    LaunchedEffect(state.success) {
        if (state.success) {
            showSuccessDialog = true
            viewModel.clearSuccess()
        }
    }

    // onSave
    fun onSave() {
        scope.launch {
            viewModel.updateProfile(
                name      = username.takeIf{it.isNotBlank()},
                birthDate = birthDate.takeIf{dateChanged}
            )
            pendingPhotoUri?.let{ viewModel.updateProfilePhoto(it, context) }
            Firebase.auth.currentUser
                ?.updateProfile(userProfileChangeRequest{ displayName=username })
                ?.await()
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFC3E2FA),Color.White)))
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
                        model = ImageRequest.Builder(context).data(photoUrl?:R.drawable.imagenprueba).crossfade(true).build(),
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable{ openGallery() }
                    )
                    Spacer(Modifier.height(10.dp))
                    SecondaryButton("CAMBIAR FOTO", { openGallery() }, Modifier.width(170.dp).height(38.dp))
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            clip = false
                        )
                        .background(
                            color = Color(0xFFdeedf4),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Subtitle("Nombre de usuario", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username=it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor=Color.White,unfocusedContainerColor=Color.White)
                    )

                    Spacer(Modifier.height(10.dp))
                    Subtitle("Correo electrónico", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = Firebase.auth.currentUser?.email.orEmpty(),
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor=Color.White,
                            unfocusedContainerColor=Color.White,
                            disabledContainerColor=Color.White,
                            disabledBorderColor=MaterialTheme.colorScheme.outline
                        ),
                        supportingText = { Text("El correo electrónico no es editable",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant) }
                    )

                    Spacer(Modifier.height(10.dp))
                    Subtitle("Fecha de nacimiento", Modifier.align(Alignment.Start))
                    OutlinedTextField(
                        value = birthDate,
                        onValueChange={},
                        readOnly=true,
                        singleLine=true,
                        modifier=Modifier.fillMaxWidth(),
                        trailingIcon={ IconButton({ datePicker.show() }){ Icon(Icons.Default.CalendarToday,null) }},
                        shape=RoundedCornerShape(12.dp),
                        colors=OutlinedTextFieldDefaults.colors(focusedContainerColor=Color.White,unfocusedContainerColor=Color.White)
                    )
                    Text("Debes tener al menos 18 años",style=MaterialTheme.typography.bodySmall,
                        color=if(dateValid)MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        modifier=Modifier.padding(start=16.dp,top=4.dp)
                    )

                    Spacer(Modifier.height(10.dp))
                    Subtitle("Contraseña", Modifier.align(Alignment.Start))
                    if (canChange) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { navController.navigate("changePassword") }
                                .padding(16.dp)
                        ) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                                Text("••••••••••••••••",style=MaterialTheme.typography.bodyLarge)

                            }
                        }
                    } else {
                        Text("Tu cuenta se autentica con Google; no puedes cambiar contraseña aquí.",
                            style=MaterialTheme.typography.bodyMedium,
                            color=MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier=Modifier.fillMaxWidth().padding(12.dp))
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth().padding(top=24.dp), horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick={ onSave() },
                            enabled=hasChanges && dateValid,
                            modifier=Modifier.weight(1f).height(40.dp),
                            colors=ButtonDefaults.buttonColors(
                                containerColor=Color(0xFF3EA8FE),
                                disabledContainerColor=Color(0xFF3EA8FE).copy(alpha=0.3f),
                                contentColor=Color.White, disabledContentColor=Color.White.copy(alpha=0.6f)
                            ),
                            shape=RoundedCornerShape(12.dp)
                        ){
                            Text("Guardar")
                        }
                        SecondaryButton("Cancelar",{ navController.popBackStack() },Modifier.weight(1f).height(40.dp))
                    }
                }
            }
        }

        // Error toast
        state.error?.let{ err->
            LaunchedEffect(err){
                Toast.makeText(context,err,Toast.LENGTH_LONG).show()
                viewModel.consumeError()
            }
        }

        // Success modal
        ModalInfoDialog(
            visible=showSuccessDialog,
            message="¡Se guardaron los cambios!",
            primaryButton=DialogButton("Aceptar"){
                showSuccessDialog=false
                navController.popBackStack()
            }
        )
    }
}
