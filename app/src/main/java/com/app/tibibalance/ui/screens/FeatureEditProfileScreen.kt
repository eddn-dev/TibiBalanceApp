package com.app.tibibalance.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.*
import androidx.compose.foundation.layout.Arrangement


@Composable
fun FeatureEditProfileScreen() {
    //Degradado del background
    GradientBackgroundScreen {
        // Contenido de la pantalla
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Header(
                title = "Editar Información Personal",
                // Si se ingresa a esta pantalla desde los ajustes, el boton de regreso onBackClick lleva a ajustes, no a Visualizar perfil *
                showBackButton = true,
                onBackClick = { }, //Redireccionar a Visualizar perfil
                profileImage = null
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                ProfileContainer(
                    imageResId = R.drawable.imagenprueba,
                    size = 110.dp,
                    contentDescription = "Foto de perfil"
                )

                // Este es otro botón? Falta el componente? <- - - - - - - - alargar componente
                SecondaryButton(
                    modifier = Modifier.padding(top=10.dp),
                    text = "Cambiar Foto",
                    onClick = { } //Cambiar Foto de perfil
                )
            }
            Column(
                modifier = Modifier.padding(top = 270.dp)
            ) {
                Subtitle(
                    text = "Nombre de usuario:"
                )
                InputText(
                    value = "nora soto", //Colocar el nombre del usuario desde Firebase
                    onValueChange = {  },
                    modifier = Modifier.padding(top = 10.dp)
                )
                //Falta icono de editar (lapiz)

                Subtitle(
                    text = "Correo electrónico:",
                    modifier = Modifier.padding(top=20.dp)
                )
                InputText(
                    value = "norasoto5@gmail.com", //Colocar el nombre del usuario desde Firebase
                    onValueChange = {  },
                    modifier = Modifier.padding(top = 10.dp)
                )
                Caption(
                    text = "El correo electrónico no es editable"
                )

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
                //Falta icono de editar (lapiz)

                Subtitle(
                    text = "Contraseña:",
                    modifier = Modifier.padding(top=20.dp)
                )
                InputText(
                    value = "********", //Colocar contraseña encriptada desde Firebase
                    onValueChange = {  },
                    modifier = Modifier
                        .padding(top = 10.dp)
                )
                //Falta icono de editar (lapiz)
                Box(modifier = Modifier.fillMaxSize()){
                    Row (
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top=50.dp), // Padding externo (alrededor de toda la fila)

                        horizontalArrangement = Arrangement.spacedBy(30.dp), // Espacio entre los elementos de la fila
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        SecondaryButton(
                            text = "Guardar",
                            onClick = {} //Mostrar mensaje de confirmacion o warning (si no se hicieron cambios)
                        )

                        SecondaryButton(
                            text = "Cancelar",
                            onClick = {} //Mostrar mensaje de alerta
                        )
                    }
                }
                //Agregar navbar
            }
        }
    }

}