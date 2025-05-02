package com.app.tibibalance.ui.screens

import android.widget.ProgressBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


@Composable
fun FeatureMyAchivementsScreen() {
    // Contenido de la pantalla
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackgroundScreen {
            // Contenido de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
            )
            {
                Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 80.dp)
                )
                {
                    FormContainer(
                        modifier = Modifier
                            .height(734.dp)  // Establece un alto específico
                            .padding(20.dp)
                    )
                    {
                        //Mostrar todos los logros cargados en Firebase (agregar un for aqui) - - - - -> Firebase
                        Row() {
                            AchievementContainer(
                                icon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.racha),
                                        contentDescription = "Ícono de logro"
                                    )
                                },
                                title = "Racha", //Cambiar por el nombre del i-esimo logro
                                description = "Usaste la app 3 días seguidos", //Cambiar por la descripción del i-esimo logro
                                percent = 75 //Cambiar por el porcentaje del usuario en el i-ésimo Firebase
                            )
                        }
                    }

                }
            }
            }
        }
        Header(
            title = "Mis Logros",
            // Si se ingresa a esta pantalla desde los ajustes, el boton de regreso onBackClick lleva a ajustes, no a Visualizar perfil *
            showBackButton = true,
            onBackClick = { }, //Redireccionar a Visualizar perfil
            profileImage = null
        )
}