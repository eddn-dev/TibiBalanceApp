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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components. buttons.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ConfigureNotificationScreen(
    onNavigateUp: () -> Unit = {},
    //viewModel: ConfigureNotificationViewModel = hiltViewModel()
){
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )
    var showModal by remember { mutableStateOf(false) }

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
            //verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(10.dp))

            FormContainer{



                Spacer(modifier = Modifier.height(15.dp))

                /*******************************MSGS PERSONALIZADOS*******************************/
                FormContainer(
                    backgroundColor = Color.White,
                    modifier = Modifier.padding(bottom = 15.dp)
                ){
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ImageContainer(
                            resId = R.drawable.ic_msg_image,
                            contentDescription = null,
                            modifier = Modifier
                                .width(38.dp)
                                .height(38.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                /*********************************************************************************/


                Spacer(Modifier.height(8.dp))

                FormContainer (
                    backgroundColor = Color(0xFFAED3E3)
                ){
                    Subtitle(
                        text = "Hábitos",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(10.dp))


                /*******************************NOTI BEBER 2LT DE AGUA*******************************/
                FormContainer(
                    backgroundColor = Color.White,
                    modifier = Modifier.padding(bottom = 15.dp)
                ){

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ImageContainer(
                            resId = R.drawable.iconwaterimage,
                            contentDescription = null,
                            modifier = Modifier
                                .width(38.dp)
                                .height(38.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Subtitle(text = "Beber 2 litros de")
                            Subtitle(text = "agua al día")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        var isWaterNotificationOn by remember { mutableStateOf(true) }
                        IconButton(
                            resId = if (isWaterNotificationOn) R.drawable.iconbellswitch_on else R.drawable.iconbellswitch_off,
                            contentDescription = if (isWaterNotificationOn) "Notificación activada" else "Notificación desactivada",
                            onClick = { isWaterNotificationOn = !isWaterNotificationOn }
                        )

                        IconButton(
                            resId = R.drawable.icon_edit_blue,// cambiar de imagen
                            contentDescription = "Campana apagada",
                            onClick = {showModal = true}
                        )
                    }

                }
                /************************************************************************************/


                Spacer(Modifier.height(10.dp))


                /*******************************NOTI DORMIR 7 HRS AL DIA*******************************/
                FormContainer(
                    backgroundColor = Color.White,
                    modifier = Modifier.padding(bottom = 15.dp)
                ){
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ImageContainer(
                            resId = R.drawable.iconsleepimage,
                            contentDescription = null,
                            modifier = Modifier
                                .width(38.dp)
                                .height(38.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Subtitle(text = "Dormir mínimo 7")
                            Subtitle(text = "horas al día")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        var isSleepNotificationOn by remember { mutableStateOf(true) }
                        IconButton(
                            resId = if (isSleepNotificationOn) R.drawable.iconbellswitch_on else R.drawable.iconbellswitch_off,
                            contentDescription = if (isSleepNotificationOn) "Notificación activada" else "Notificación desactivada",
                            onClick = { isSleepNotificationOn = !isSleepNotificationOn }
                        )

                        IconButton(
                            resId = R.drawable.icon_edit_blue,
                            contentDescription = "Campana apagada",
                            onClick = {showModal = true }
                        )

                    }
                }
                /************************************************************************************/


                Spacer(Modifier.height(10.dp))


                /*******************************NOTI LEER 20 PAG AL DIA*******************************/
                FormContainer(
                    backgroundColor = Color.White,
                    modifier = Modifier.padding(bottom = 25.dp)
                ){
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ImageContainer(
                            resId = R.drawable.iconbookimage,
                            contentDescription = null,
                            modifier = Modifier
                                .width(38.dp)
                                .height(38.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Subtitle(text = "Leer 20 páginas")
                            Subtitle(text = "de un libro")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        var isReadNotificationOn by remember { mutableStateOf(true) }

                        IconButton(
                            resId = if (isReadNotificationOn) R.drawable.iconbellswitch_on else R.drawable.iconbellswitch_off,
                            contentDescription = if (isReadNotificationOn) "Notificación activada" else "Notificación desactivada",
                            onClick = { isReadNotificationOn = !isReadNotificationOn}
                        )

                        IconButton(
                            resId = R.drawable.icon_edit_blue,
                            contentDescription = "Campana apagada",
                            onClick = {showModal = true }
                        )
                    }
                }
                /************************************************************************************/


                Spacer(Modifier.height(10.dp))



            }

        }
        Header(
            title = "Notificaciones",
            showBackButton = true,
            onBackClick = onNavigateUp,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }

    // SIMPLEMENTE ES PRUEBA DE FUNCIONAMIENTO DE LA EDICIÓN, AUN NO SE TIENE FUNCIONALIDAD
    if (showModal) {
        ModalConfigNotification(
            title              = "Configurar notificación",
            initialTime        = "8:00 a.m.",
            initialMessage     = "¡Hora de hacer el hábito!",
            initialDays        = emptySet(),
            initialRepeatValue = "",
            initialRepeatUnit  = "",
            initialTypes       = emptySet(),
            onDismissRequest   = { showModal = false },
            onSave             = { time, msg, days, repeatValue, repeatUnit, types ->
                // Aquí se podra guardar los datos
                showModal = false
            }
        )
    }


}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun PreviewViewProfileScreen() {
    ConfigureNotificationScreen()
}
