package com.app.tibibalance.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components. buttons.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ConfigureNotificationScreen(
    onNavigateUp: () -> Unit = {},
    viewModel: ConfigureNotificationViewModel = hiltViewModel()
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )
    var showModal by remember { mutableStateOf(false) }
    val habitList = viewModel.ui.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 130.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            /*** MENSAJES PERSONALIZADOS ***/
            FormContainer(
                backgroundColor = Color.White,
                modifier = Modifier.padding(bottom = 15.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ImageContainer(
                        resId = R.drawable.ic_msg_image,
                        contentDescription = null,
                        modifier = Modifier.size(38.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Subtitle(text = "Recibir Mensajes")
                        Subtitle(text = "Personatlizado")
                    }
                    var isEnabled by remember { mutableStateOf(false) }
                    SwitchToggle(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )
                }
            }

            /*** SUBTÍTULO HÁBITOS ***/
            FormContainer(backgroundColor = Color(0xFFAED3E3)) {
                Subtitle(
                    text = "Hábitos",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(10.dp))

            /*** LISTA DE HÁBITOS DEL USUARIO ***/
            if (habitList.isEmpty()) {
                Subtitle(text = "No tienes hábitos aún.")
            } else {
                habitList.forEach { habit ->
                    FormContainer(
                        backgroundColor = Color.White,
                        modifier = Modifier.padding(bottom = 15.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ImageContainer(
                                resId = R.drawable.iconwaterimage, // puedes cambiar esto luego por el icono real
                                contentDescription = null,
                                modifier = Modifier.size(38.dp)
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Subtitle(text = habit.name)
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            IconButton(
                                resId = if (habit.notifConfig.enabled) R.drawable.iconbellswitch_on else R.drawable.iconbellswitch_off,
                                contentDescription = if (habit.notifConfig.enabled) "Notificación activada" else "Notificación desactivada",
                                onClick = { }//viewModel.toggleNotification(habit) }
                            )

                            IconButton(
                                resId = R.drawable.icon_edit_blue,
                                contentDescription = "Editar notificación",
                                onClick = { showModal = true }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        Header(
            title = "Notificaciones",
            showBackButton = true,
            onBackClick = onNavigateUp,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        /*** MODAL DE CONFIGURACIÓN ***/
        if (showModal) {
            ModalConfigNotification(
                title = "Configurar notificación",
                initialTime = "8:00 a.m.",
                initialMessage = "¡Hora de hacer el hábito!",
                initialDays = emptySet(),
                initialRepeatValue = "",
                initialRepeatUnit = "",
                initialTypes = emptySet(),
                onDismissRequest = { showModal = false },
                onSave = { _, _, _, _, _, _ -> showModal = false }
            )
        }
    }
}
