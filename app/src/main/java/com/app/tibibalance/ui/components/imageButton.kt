package com.app.tibibalance.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ImageButton(
    onClick: () -> Unit,
    imageRes: Int, // Recurso de la imagen
    modifier: Modifier = Modifier,
    size: Int = 48 // Tamaño del botón
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size.dp) // Tamaño del botón
    ) {
        // Usamos Image para mostrar la imagen dentro del botón
        Image(
            painter = painterResource(id = imageRes), // Recurso de imagen
            contentDescription = "Botón de imagen",
            modifier = Modifier.size(size.dp) // Ajustar tamaño de la imagen
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ImageButtonPreview() {
    // Reemplaza el recurso con la imagen que tienes en tus recursos
    ImageButton(onClick = { /* Acción al hacer clic */ }, imageRes = android.R.drawable.ic_menu_add)
}
