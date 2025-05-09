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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ViewConfigureNotificationScreen() {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 80.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            FormContainer {

                /*******************************MSGS PERSONALIZADOS*******************************/
                FormContainer(
                    backgroundColor = Color.White
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


                Spacer(Modifier.height(2.dp))

                FormContainer (
                    backgroundColor = Color(0xFFAED3E3)
                ){
                    Subtitle(
                        text = "Hábitos",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(4.dp))


                /*******************************NOTI BEBER 2LT DE AGUA*******************************/
                FormContainer(
                    backgroundColor = Color.White
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


                        var isEnabled by remember { mutableStateOf(false) }
                        SwitchToggle(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it }
                        )
                    }
                }
                /************************************************************************************/


                Spacer(Modifier.height(4.dp))


                /*******************************NOTI DORMIR 7 HRS AL DIA*******************************/
                FormContainer(
                    backgroundColor = Color.White
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


                        var isEnabled by remember { mutableStateOf(false) }
                        SwitchToggle(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it }
                        )
                    }
                }
                /************************************************************************************/


                Spacer(Modifier.height(4.dp))


                /*******************************NOTI LEER 20 PAG AL DIA*******************************/
                FormContainer(
                    backgroundColor = Color.White
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


                        var isEnabled by remember { mutableStateOf(false) }
                        SwitchToggle(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it }
                        )
                    }
                }
                /************************************************************************************/


                Spacer(Modifier.height(10.dp))



            }
        }
        Header(
            title          = "Notificaciones",
            showBackButton = true,
            onBackClick    = {  },
            modifier       = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
fun PreviewViewProfileScreen() {
    ViewConfigureNotificationScreen()
}
