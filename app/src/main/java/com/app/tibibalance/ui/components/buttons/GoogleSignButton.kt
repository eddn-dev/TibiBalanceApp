/**
 * @file    GoogleSignButton.kt
 * @ingroup ui_components
 * @brief   Botón de inicio de sesión con Google.
 *
 * Representa un botón *outlined* con el logotipo de Google y una etiqueta
 * configurable.  Se utiliza en los flujos de autenticación para invocar
 * el inicio de sesión mediante Google Identity Services.
 *
 * @param text      Etiqueta que acompaña al icono (por defecto
 *                  «Continuar con Google»).
 * @param onClick   Lambda que se ejecuta al pulsar el botón.
 * @param modifier  Modificador opcional para ajustar tamaño o posición.
 */
package com.app.tibibalance.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R

@Composable
fun GoogleSignButton(
    text: String = "Continuar con Google",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .width(250.dp)
            .height(40.dp),
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, Color.Black),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleSignButtonPreview() {
    MaterialTheme {
        GoogleSignButton(onClick = {})
    }
}
