/**
 * @file    ImageContainer.kt
 * @ingroup ui_component // Grupo para componentes generales de UI o específicos de imagen
 * @brief   Define un [Composable] reutilizable para mostrar imágenes desde recursos drawable dentro de un contenedor estilizable.
 *
 * @details Este archivo contiene el [Composable] `ImageContainer`, que utiliza un componente
 * [Box] para contener y un componente [Image] de Jetpack Compose para mostrar una imagen
 * cargada desde los recursos drawable de la aplicación mediante `painterResource`.
 *
 * Permite configurar el color de fondo del contenedor, el radio de las esquinas para
 * el recorte ([clip]) y el modo de escalado ([ContentScale]) de la imagen para controlar
 * cómo se ajusta o recorta dentro del contenedor. Es un componente base versátil
 * para mostrar imágenes estáticas en la interfaz.
 *
 * @see androidx.compose.foundation.Image Componente base de Jetpack Compose para mostrar imágenes.
 * @see androidx.compose.foundation.layout.Box Contenedor utilizado para aplicar fondo y recorte.
 * @see androidx.compose.ui.res.painterResource Función para cargar recursos drawable.
 * @see androidx.compose.ui.layout.ContentScale Define cómo se escala la imagen.
 * @see androidx.compose.foundation.shape.RoundedCornerShape Usado para el recorte de esquinas.
 * @see androidx.annotation.DrawableRes Anotación para el ID del recurso.
 */
package com.app.tibibalance.ui.components

import androidx.annotation.DrawableRes
import com.app.tibibalance.R // Necesario para las previews
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme // Para previews
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @brief Un [Composable] que muestra una imagen desde un recurso drawable dentro de un [Box] contenedor.
 *
 * @details Renderiza una [Image] cargada mediante `painterResource(id = resId)`. La imagen
 * se muestra dentro de un [Box] que puede tener un color de fondo y esquinas redondeadas
 * aplicadas mediante `clip`. El modo de escalado de la imagen se controla con `contentScale`.
 *
 * @param resId El ID del recurso drawable (`@DrawableRes Int`) de la imagen a mostrar.
 * @param contentDescription Un [String] opcional que describe la imagen para la accesibilidad.
 * @param modifier Un [Modifier] opcional que se aplica al [Box] contenedor principal.
 * Permite personalizar el tamaño, padding, etc., desde el exterior.
 * @param backgroundColor El [Color] de fondo para el [Box] contenedor. Por defecto es [Color.Transparent].
 * @param contentScale El modo [ContentScale] que define cómo se debe escalar o recortar la imagen
 * para ajustarse a los límites del contenedor. Por defecto es [ContentScale.Fit], que asegura
 * que toda la imagen sea visible sin recortar, potencialmente dejando espacio vacío.
 * Otras opciones comunes son [ContentScale.Crop] (recorta para llenar) o [ContentScale.FillBounds] (distorsiona).
 * @param cornerRadius El radio ([Dp]) para las esquinas redondeadas que se aplicarán al contenedor
 * mediante `clip`. Por defecto es `0.dp` (sin redondeo).
 */
@Composable
fun ImageContainer(
    @DrawableRes resId: Int, // ID del recurso Drawable
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent, // Fondo transparente por defecto
    contentScale: ContentScale = ContentScale.Fit, // Escala por defecto: Fit (sin recorte)
    cornerRadius: Dp = 0.dp // Sin esquinas redondeadas por defecto
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius)) // Aplica el recorte con el radio especificado
            .background(backgroundColor) // Aplica el color de fondo
    ) {
        Image(
            painter = painterResource(id = resId), // Carga la imagen desde el recurso
            contentDescription = contentDescription, // Descripción para accesibilidad
            contentScale = contentScale, // Aplica el modo de escalado
            modifier = Modifier.fillMaxSize() // La imagen intenta llenar el Box contenedor
        )
    }
}

// --- Previews ---

/**
 * @brief Previsualización del [ImageContainer] con valores por defecto.
 */
@Preview(showBackground = true, name = "ImageContainer Default", widthDp = 150, heightDp = 150)
@Composable
private fun ImageContainerDefaultPreview() {
    MaterialTheme {
        ImageContainer(
            resId = R.drawable.ic_google_logo, // Usa un drawable de ejemplo
            contentDescription = "Logo Google (Default)",
            modifier = Modifier.size(100.dp) // Tamaño para la preview
            // Usa backgroundColor, contentScale y cornerRadius por defecto
        )
    }
}

/**
 * @brief Previsualización del [ImageContainer] con fondo, esquinas redondeadas y ContentScale.Crop.
 */
@Preview(showBackground = true, name = "ImageContainer Custom", widthDp = 150, heightDp = 150)
@Composable
private fun ImageContainerCustomPreview() {
    MaterialTheme {
        ImageContainer(
            resId = R.drawable.ic_launcher_foreground, // Usa otro drawable de ejemplo
            contentDescription = "Launcher Icon (Custom)",
            modifier = Modifier.size(120.dp), // Tamaño diferente
            backgroundColor = MaterialTheme.colorScheme.primaryContainer, // Color de fondo del tema
            contentScale = ContentScale.Crop, // Recorta la imagen para llenar el contenedor
            cornerRadius = 16.dp // Esquinas redondeadas
        )
    }
}

/**
 * @brief Previsualización del [ImageContainer] mostrando cómo se ve con ContentScale.FillBounds.
 */
@Preview(showBackground = true, name = "ImageContainer FillBounds", widthDp = 200, heightDp = 100)
@Composable
private fun ImageContainerFillBoundsPreview() {
    MaterialTheme {
        ImageContainer(
            resId = R.drawable.ic_google_logo,
            contentDescription = "Logo Google (FillBounds)",
            modifier = Modifier.size(width = 180.dp, height = 80.dp), // Tamaño no cuadrado
            contentScale = ContentScale.FillBounds, // Estira la imagen para llenar
            cornerRadius = 8.dp,
            backgroundColor = Color.LightGray // Fondo para ver los límites
        )
    }
}