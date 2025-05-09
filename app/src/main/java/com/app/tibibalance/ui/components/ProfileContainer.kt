/**
 * @file    ProfileContainer.kt
 * @ingroup ui_component // O ui_component_display si se prefiere más granularidad
 * @brief   Define un [Composable] reutilizable para mostrar imágenes de perfil circulares (avatares).
 *
 * @details Este archivo contiene el [Composable] `ProfileContainer`, diseñado específicamente
 * para mostrar imágenes de perfil o avatares. Utiliza un [Box] recortado con [CircleShape]
 * para crear el contenedor circular y un componente [Image] interno para mostrar la imagen
 * proporcionada desde los recursos drawable.
 *
 * La imagen interna utiliza [ContentScale.Crop] para asegurar que llene completamente el
 * círculo, recortando las partes de la imagen que excedan. Permite personalizar el tamaño
 * del círculo y el color de fondo, que es visible si la imagen tiene transparencias o
 * actúa como un placeholder visual antes de que la imagen cargue (aunque este componente
 * no maneja la carga asíncrona por sí mismo).
 *
 * @see androidx.compose.foundation.Image Componente para mostrar la imagen.
 * @see androidx.compose.foundation.layout.Box Contenedor para aplicar forma y fondo.
 * @see androidx.compose.foundation.shape.CircleShape Utilizada para el recorte circular.
 * @see androidx.compose.ui.res.painterResource Función para cargar el recurso drawable.
 * @see androidx.compose.ui.layout.ContentScale Define cómo se escala/recorta la imagen.
 * @see androidx.annotation.DrawableRes Anotación para el ID del recurso.
 */
package com.app.tibibalance.ui.components

import com.app.tibibalance.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme // Para Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.annotation.DrawableRes // Importar para el parámetro

/**
 * @brief Un [Composable] que muestra una imagen de recurso dentro de un contenedor circular.
 *
 * @details Ideal para avatares o fotos de perfil. Renderiza una [Image] cargada desde `imageResId`
 * dentro de un [Box] con recorte circular (`CircleShape`). La imagen se escala usando
 * `ContentScale.Crop` para llenar el círculo. Se puede configurar el tamaño y un color
 * de fondo para el contenedor.
 *
 * @param imageResId El ID del recurso drawable (`@DrawableRes Int`) de la imagen a mostrar.
 * @param size El diámetro ([Dp]) del contenedor circular. Por defecto `96.dp`.
 * @param backgroundColor El [Color] de fondo para el [Box] circular. Útil como placeholder
 * o si la imagen tiene transparencias. Por defecto `Color.LightGray`.
 * @param contentDescription Un [String] opcional que describe la imagen para la accesibilidad.
 * @param modifier Un [Modifier] opcional para aplicar al [Box] contenedor.
 */
@Composable
fun ProfileContainer(
    @DrawableRes imageResId: Int, // Asegura que sea un ID de Drawable
    size: Dp = 96.dp, // tamaño del círculo, default 96dp
    backgroundColor: Color = Color.LightGray, // Fondo gris claro por defecto
    contentDescription: String? = null, // Descripción opcional
    modifier: Modifier = Modifier // Modificador estándar
) {
    Box(
        modifier = modifier
            .size(size) // Aplica el tamaño especificado
            .clip(CircleShape) // Recorta el contenido a un círculo
            .background(backgroundColor), // Aplica el color de fondo
        contentAlignment = Alignment.Center // Centra la imagen dentro del Box
    ) {
        Image(
            painter = painterResource(id = imageResId), // Carga la imagen
            contentDescription = contentDescription, // Descripción para accesibilidad
            contentScale = ContentScale.Crop, // Escala la imagen para llenar y recortar si es necesario
            modifier = Modifier.fillMaxSize() // La imagen intenta llenar el Box
        )
    }
}

// --- Preview ---

/**
 * @brief Previsualización del [ProfileContainer] con valores por defecto.
 */
@Preview(showBackground = true, name = "ProfileContainer Default")
@Composable
private fun ProfileContainerPreview() {
    MaterialTheme {
        ProfileContainer(
            imageResId = R.drawable.imagenprueba, // Usa tu imagen de prueba
            contentDescription = "Avatar por defecto"
            // Usa tamaño y color de fondo por defecto
        )
    }
}

/**
 * @brief Previsualización del [ProfileContainer] con tamaño y color personalizados.
 */
@Preview(showBackground = true, name = "ProfileContainer Custom")
@Composable
private fun ProfileContainerCustomPreview() {
    MaterialTheme {
        ProfileContainer(
            imageResId = R.drawable.imagenprueba,
            contentDescription = "Avatar pequeño azul",
            size = 64.dp, // Tamaño más pequeño
            backgroundColor = MaterialTheme.colorScheme.primaryContainer // Color de fondo del tema
        )
    }
}