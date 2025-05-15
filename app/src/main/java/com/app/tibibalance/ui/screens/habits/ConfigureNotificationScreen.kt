package com.app.tibibalance.ui.screens.habits

//import android.util.Log
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
import com.app.tibibalance.ui.components.buttons.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import com.app.tibibalance.ui.components.inputs.iconByName
import androidx.compose.runtime.mutableStateMapOf


// Para representar íconos vectoriales


@Composable
fun ConfigureNotificationScreen(
    onNavigateUp: () -> Unit = {},
    viewModel: ConfigureNotificationViewModel = hiltViewModel()
) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    val habitList = viewModel.ui.collectAsState().value

    val originalMessages = remember { mutableStateMapOf<String, String>() }

    var isEnabled by remember { mutableStateOf(true) }

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
                   // var isEnabled by remember { mutableStateOf(false) }   //FALATA AGREGAR FUNCIONALIDAD A ESTE SWITCH

                    /*******************************************************************************/
                    SwitchToggle(
                        checked = isEnabled,
                        onCheckedChange = { enabled ->
                            isEnabled = enabled
                            habitList.forEach { habit ->
                                val currentMessage = habit.notifConfig.message
                                val newMessage = if (enabled) {
                                    originalMessages[habit.id] ?: currentMessage
                                } else {
                                    originalMessages[habit.id] = currentMessage
                                    "¡Hora de completar tu hábito!"
                                }

                                viewModel.forceUpdateMessage(habit, newMessage)
                            }
                        }
                    )

                    /*******************************************************************************/
                    /*SwitchToggle(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it }
                    )*/
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
                Spacer(Modifier.height(15.dp))
                Subtitle(text = "No tienes hábitos aún.")
            } else {
                habitList.forEach { habit ->


                    FormContainer(
                        backgroundColor = Color.White,
                        modifier = Modifier.padding(bottom = 15.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconContainer(
                                icon = iconByName(habit.icon),
                                contentDescription = habit.name,
                                modifier = Modifier.size(38.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier.weight(1f) // Esta parte se adapta al espacio disponible
                            ) {
                                Subtitle(
                                    text = habit.name,
                                    maxLines = 2 // Puedes limitar líneas si lo deseas
                                )
                            }

                            // Botones laterales
                            IconButton(
                                resId = if (habit.notifConfig.enabled) R.drawable.iconbellswitch_on else R.drawable.iconbellswitch_off,
                                contentDescription = if (habit.notifConfig.enabled) "Notificación activada" else "Notificación desactivada",
                                onClick = { viewModel.toggleNotification(habit) }
                            )

                            IconButton(
                                resId = R.drawable.icon_edit_blue,
                                contentDescription = "Editar notificación",
                                onClick = { viewModel.selectHabit(habit) }
                            )
                        }

                    }//formContainer
                }//Lista de habitos
            }//else list de habitos

            Spacer(Modifier.height(12.dp))
        }//column

        Header(
            title = "Notificaciones",
            showBackButton = true,
            onBackClick = onNavigateUp,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        /*** MODAL DE CONFIGURACIÓN ***/

        val selectedHabit by viewModel.selectedHabit.collectAsState()

        if (selectedHabit != null) {
            val notif = selectedHabit!!.notifConfig

            // Log para verificar qué datos estás recibiendo
           //Log.d("Modal", "Habit seleccionado: ${selectedHabit!!.name}")
            //Log.d("Modal", "Horas: ${notif.timesOfDay}")
            //Log.d("Modal", "Mensaje: ${notif.message}")
            //Log.d("Modal", "Días: ${notif.weekDays.days}")
            //Log.d("Modal", "Modo: ${notif.mode}, Vibrate: ${notif.vibrate}")


            // Mapear Set<Int> (1–7) a Set<Day>
            val selectedDays: Set<Day> = notif.weekDays.days.mapNotNull {
                when (it) {
                    1 -> Day.L
                    2 -> Day.M
                    3 -> Day.MI
                    4 -> Day.J
                    5 -> Day.V
                    6 -> Day.S
                    7 -> Day.D
                    else -> null
                }
            }.toSet()

            // Mapear NotifMode + vibrate a NotifyType
            ModalConfigNotification(
                initialTime         = notif.timesOfDay.firstOrNull()?.let { convertTo12hFormat(it) } ?: "8:00 a.m.",
                initialMessage      = notif.message,
                initialDays         = selectedDays,
                initialMode         = notif.mode,
                initialRepeatPreset = selectedHabit!!.repeatPreset,
                onDismissRequest    = { viewModel.clearSelectedHabit() },
                onSave = { time, msg, days, mode, vibrate, repeatPreset ->
                    viewModel.updateNotificationConfig(
                        habit = selectedHabit!!,
                        time = time,
                        message = msg,
                        days = days,
                        mode = mode,
                        vibrate = vibrate,
                        repeatPreset = repeatPreset
                    )
                }
            )

        }

        /************************************************************************/
    }
}

fun convertTo12hFormat(time: String): String {
    val (hourStr, minuteStr) = time.split(":")
    val hour = hourStr.toInt()
    val minute = minuteStr
    val suffix = if (hour < 12) "a.m." else "p.m."
    val hour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    return "$hour12:$minute $suffix"
}

