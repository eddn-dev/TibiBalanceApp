package com.app.tibibalance.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FeatureViewProfileScreen() {
    GradientBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(
                title = "Visualizar Perfil",
                showBackButton = true,
                onBackClick = { },
                profileImage = null
            )

            Spacer(modifier = Modifier.height(20.dp))
            FormContainer(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                ProfileContainer(
                    imageResId = R.drawable.imagenprueba,
                    size = 110.dp,
                    contentDescription = "Foto de perfil"
                )
                Spacer(modifier = Modifier.height(12.dp))
                Subtitle(text = "Nora Soto")

                Spacer(modifier = Modifier.height(20.dp))
                /*Subtitle(text = "Fecha de Nacimiento:")
                InputDate(
                    selectedDate = "29/07/2004",
                    onDateSelected = {}, // Puedes desactivar esto si no quieres que sea editable
                )*/

                Subtitle(
                    text = "Fecha de nacimiento:",
                    modifier = Modifier.padding(top=20.dp)
                )
                //Falta incono de calendario
                InputText(
                    value = "29/07/2004", //Colocar el nombre del usuario desde Firebase
                    onValueChange = {  },
                    modifier = Modifier.padding(top = 10.dp)
                )

                Subtitle(
                    text = "Correo electrónico:",
                    modifier = Modifier.padding(top=20.dp)
                )
                InputText(
                    value = "norasoto5@gmail.com", //Colocar el nombre del usuario desde Firebase
                    onValueChange = {  },
                    modifier = Modifier.padding(top = 10.dp)
                )

                Box(modifier = Modifier.fillMaxSize()){
                    Row (
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top=50.dp), // Padding externo (alrededor de toda la fila)

                        horizontalArrangement = Arrangement.spacedBy(30.dp), // Espacio entre los elementos de la fila
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        SecondaryButton(
                            text = "Editar perfil",
                            onClick = {}
                        )

                        SecondaryButton(
                            text = "Cerrar sesión",
                            onClick = {}
                        )
                    }
                }

                /*Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryButton(
                        text = "Editar perfil",
                        onClick = { /* Navegar a FeatureEditProfileScreen */ },
                        modifier = Modifier.weight(1f)
                    )
                    SecondaryButton(
                        text = "Cerrar sesión",
                        onClick = { /* Acción de logout */ },
                        modifier = Modifier.weight(1f)
                    )
                }*/
            }
        }
    }
}
