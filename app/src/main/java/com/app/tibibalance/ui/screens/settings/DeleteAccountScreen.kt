package com.app.tibibalance.ui.screens.settings

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.tibibalance.ui.components.buttons.DangerButton
import com.app.tibibalance.ui.navigation.Screen // ✅ Importa Screen
import com.app.tibibalance.ui.screens.profile.EditProfileViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    navController: NavHostController,
    viewModel: EditProfileViewModel = hiltViewModel(),
    isGoogleUser: Boolean
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var password by remember { mutableStateOf("") }
    var stepVerified by remember { mutableStateOf(false) }

    val googleSignInHelper = remember { GoogleSignInHelper(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val idToken = googleSignInHelper.getCredentialFromIntent(result.data)
            if (idToken != null) {
                viewModel.reauthenticateUserWithGoogle(idToken)
            } else {
                Toast.makeText(context, "No se pudo obtener el token de Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(state.success) {
        if (state.success && !stepVerified) {
            stepVerified = true
            viewModel.clearSuccess()
        } else if (state.success && stepVerified) {
            viewModel.clearSuccess()
            navController.navigate(Screen.Launch.route) {
                popUpTo(Screen.Main.route) {
                    this@popUpTo.inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.consumeError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Eliminar cuenta",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(16.dp))

        if (!stepVerified) {
            if (!isGoogleUser) {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(12.dp))

                DangerButton(
                    text = "Confirmar contraseña",
                    onClick = {
                        keyboardController?.hide()
                        scope.launch {
                            viewModel.reauthenticateUserWithPassword(password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                DangerButton(
                    text = "Verificar con cuenta Google",
                    onClick = {
                        googleSignInHelper.getSignInIntent(
                            onSuccess = { sender ->
                                launcher.launch(IntentSenderRequest.Builder(sender).build())
                            },
                            onError = {
                                Toast.makeText(context, "Error con Google One Tap", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            DangerButton(
                text = "Eliminar cuenta definitivamente",
                onClick = {
                    keyboardController?.hide()
                    viewModel.deleteUserAccount()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Cancelar")
        }
    }
}
