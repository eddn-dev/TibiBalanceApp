package com.app.tibibalance.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

import com.app.tibibalance.ui.components.Description
import com.app.tibibalance.ui.components.FormContainer
import com.app.tibibalance.ui.components.Header

import com.app.tibibalance.ui.components.InputSelect
import com.app.tibibalance.ui.components.InputText
import com.app.tibibalance.ui.components.SecondaryButton
import com.app.tibibalance.ui.components.Subtitle
import com.app.tibibalance.ui.components.ImageButton

@Composable
fun FeatureAddHabitScreen() {
    // Contenido de la pantalla
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackgroundScreen {
            // Contenido de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                //.background(Color(0xFFf5f5f5)) // Fondo gris claro
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                ) {


                    FormContainer(
                        modifier = Modifier
                            .height(850.dp)  // Establece un alto específico
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))

                        Subtitle(
                            text = "Crear mi propio hábito",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(Alignment.CenterVertically),
                            textAlign = TextAlign.Center
                        )


                        ImageButton(
                            onClick = { /* Acción */ },
                            imageRes = com.app.tibibalance.R.drawable.ic_button_habit,
                            modifier = Modifier
                                .width(100.dp) // Cambia el ancho
                                .height(100.dp) // Cambia el alto
                            ,
                            size = 64, // Tamaño de la imagen interna (puedes dejar este valor si no quieres cambiarlo)
                        )


                            Description(
                            text = "Nombre: *",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        var nombre by remember { mutableStateOf("Nora Soto") }
                        InputText(
                            value = nombre,
                            onValueChange = { nombre = it }
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        Description(
                            text = "Descripción:",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        var descripcion by remember { mutableStateOf("") }
                        InputText(
                            value = descripcion,
                            onValueChange = { descripcion = it }
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        Description(
                            text = "frecuencia: *",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        var frecuencia by remember { mutableStateOf("Diario") }
                        InputSelect(
                            options = listOf("Diario", "Semanal", "Mensual"),
                            selectedOption = frecuencia,
                            onOptionSelected = { frecuencia = it }
                        )
                        Spacer(modifier = Modifier.height(2.dp))

                        Description(
                            text = "Duración:",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        var duracion by remember { mutableStateOf("") }
                        InputText(
                            value = duracion,
                            onValueChange = { duracion = it }
                        )
                        Spacer(modifier = Modifier.height(2.dp))


                        Description(
                            text = "Categoría: *",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        var categoria by remember { mutableStateOf("Diario") }
                        InputSelect(
                            options = listOf("Diario", "Semanal", "Mensual"),
                            selectedOption = categoria,
                            onOptionSelected = { categoria = it }
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Description(
                                text = "Notificación: *",
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                           /* ImageButton(
                                onClick = { /* Acción */ },
                                imageRes = com.app.tibibalance.R.drawable.ic_button_notification,
                                modifier = Modifier
                                    .width(64.dp) // Cambia el ancho
                                    .height(64.dp) // Cambia el alto
                                ,
                               size = 64, // Tamaño de la imagen interna (puedes dejar este valor si no quieres cambiarlo)
                            )*/

                        }


                        Spacer(modifier = Modifier.height(40.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Spacer(modifier = Modifier.width(8.dp))
                            SecondaryButton(
                                text = "Guardar",
                                onClick = {},
                                backgroundColor = Color.White
                            )

                            Spacer(modifier = Modifier.width(20.dp)) // 1f es un peso relativo

                            SecondaryButton(
                                text = "Cancelar",
                                onClick = {},
                                backgroundColor = Color.White
                            )
                        }

                    }

                }
            }
        }

        // 2. Header encima del gradiente (posición fija arriba)
        Header(
            title = "Agregar Hábito",
            showBackButton = false,
            profileImage = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
        )

    }
}