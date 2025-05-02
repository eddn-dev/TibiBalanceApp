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

@Composable
fun FeatureMyAchivementsScreen() {
    //Degradado del background
    GradientBackgroundScreen {
        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Header(
                title = "Mis Logros",
                // Si se ingresa a esta pantalla desde los ajustes, el boton de regreso onBackClick lleva a ajustes, no a Visualizar perfil *
                showBackButton = true,
                onBackClick = { }, //Redireccionar a Visualizar perfil
                profileImage = null
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ){
                Row() {
                    /*AchivementContainer( - - - - - - - - - - -> Falta componente

                    )*/
                    //Racha
                    //Usaste la app 3 días seguidos.q
                    ImageContainer(
                        imageResId = R.drawable.racha, //Mandar el nombre de la img desde Firebase
                        contentDescription = "racha", //Nombre del logro
                        modifier = Modifier.size(100.dp)
                    )
                    ProgressBar(percent = 0) //Enviar el progreso del logro desde Firebase
                }
            }
        }
    }
}