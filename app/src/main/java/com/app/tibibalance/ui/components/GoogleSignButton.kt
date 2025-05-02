package com.app.tibibalance.ui.components

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
        ) { //Usar la imagen del logo de google. Debes tener una imagen con el nombre "ic_google_logo" en res/drawable
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = "Google logo",
                modifier = Modifier
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoogleSignButtonPreview() {
    MaterialTheme {
        GoogleSignButton(
            onClick = { }
        )
    }
}
