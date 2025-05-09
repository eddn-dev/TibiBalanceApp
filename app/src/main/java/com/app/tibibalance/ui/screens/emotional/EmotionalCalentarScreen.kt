/**
 * @file    EmotionalCalendarScreen.kt
 * @ingroup ui_screens_emotional // Grupo para pantallas relacionadas con el estado emocional
 * @brief   Define el [Composable] para la pantalla del calendario emocional.
 *
 * @details
 * Esta pantalla permite a los usuarios registrar y visualizar su estado emocional a lo largo
 * de un mes. Presenta una interfaz de calendario donde cada día puede mostrar un icono
 * representando la emoción registrada.
 *
 * Funcionalidades Principales:
 * - **Visualización del Mes Actual:** Muestra un título con el mes y año.
 * - **Cuadrícula de Calendario ([CalendarGrid]):** Renderiza los días del mes, permitiendo
 * la visualización de iconos de emoción en cada día. Los días son interactivos
 * (invocan `onDayClick`).
 * - **Registro de Emoción (Implícito):** Aunque no se muestra el modal de selección
 * de emoción aquí, la pantalla está diseñada para que al hacer clic en un día (`onDayClick`),
 * se pueda abrir dicho modal.
 * - **Enlace "Saber Más":** Un [TextButtonLink] que podría dirigir a una sección
 * informativa sobre el seguimiento emocional.
 * - **Resumen Emocional:** Muestra la emoción más frecuente del mes y cuántos días
 * se ha registrado, utilizando un [HabitContainer] para la visualización.
 *
 * La UI utiliza un fondo degradado y componentes reutilizables como [Title], [Subtitle],
 * [CalendarGrid], [TextButtonLink], [ImageContainer] y [HabitContainer].
 * Los datos del calendario (días y emociones) y la emoción más frecuente son actualmente
 * datos de ejemplo (`sampleDays`, `mostFrequentEmotionDays`, `mostFrequentEmotionRes`)
 * y deberían ser proporcionados por un ViewModel en una implementación real.
 *
 * @see CalendarGrid Componente reutilizable para mostrar la cuadrícula del calendario.
 * @see EmotionDay Data class que representa la información de un día en el calendario.
 * @see HabitContainer Usado para mostrar el resumen de la emoción más frecuente.
 * @see ImageContainer Usado para los iconos de emoción.
 * @see Title, Subtitle Componentes de texto para encabezados.
 * @see TextButtonLink Componente para el enlace "SABER MÁS".
 * @see R.drawable Contiene los recursos de iconos de emoción (e.g., `iconhappyimage`).
 */
// src/main/java/com/app/tibibalance/ui/screens/emotional/EmotionalCalendarScreen.kt
package com.app.tibibalance.ui.screens.emotional

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.* // Asumiendo que CalendarGrid, EmotionDay están aquí o en subpaquetes
import com.app.tibibalance.ui.components.containers.HabitContainer // Asegúrate que la ruta es correcta
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title

/**
 * @brief Composable principal para la pantalla del calendario de seguimiento emocional.
 *
 * @details Muestra un calendario mensual donde los usuarios pueden registrar y ver sus
 * estados emocionales. Incluye una sección para la emoción más frecuente del mes.
 *
 * @param onKnowMore Callback que se invoca cuando el usuario pulsa el enlace "SABER MÁS ?".
 * @param onDayClick Callback que se invoca cuando el usuario pulsa sobre un día en el
 * calendario. Recibe el número del día ([Int]) como parámetro.
 * @param mostFrequentEmotionDays El número de días ([Int]) que la emoción más frecuente
 * se ha registrado en el mes actual. Por defecto es 5.
 * @param mostFrequentEmotionRes El ID del recurso drawable ([Int]) para el icono de la
 * emoción más frecuente. Por defecto `R.drawable.iconhappyimage`.
 */
@Composable
fun EmotionalCalentarScreen(
    onKnowMore: () -> Unit = {},
    onDayClick: (day: Int) -> Unit = {},
    mostFrequentEmotionDays: Int = 5,
    @DrawableRes mostFrequentEmotionRes: Int = R.drawable.iconhappyimage // Anotar con @DrawableRes
) {
    // Define un fondo degradado para la pantalla, desde un azul claro a blanco.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    // Contenedor Box principal que ocupa toda la pantalla y aplica el fondo.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        // Columna principal para organizar el contenido, permite desplazamiento vertical.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Habilita el scroll si el contenido excede la altura.
                .padding(16.dp), // Padding general para el contenido.
            verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio entre elementos principales.
        ) {
            // Contenedor principal de la UI del calendario, con fondo celeste y bordes redondeados.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFAED3E3).copy(alpha = .45f), // Fondo celeste semitransparente.
                        shape = RoundedCornerShape(16.dp) // Bordes redondeados.
                    )
                    .padding(16.dp), // Padding interno del contenedor.
                verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre secciones internas.
            ) {
                // "Píldora" o cabecera con el título principal.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF85C3DE), // Fondo azul más opaco para la píldora.
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(vertical = 8.dp), // Padding vertical para la píldora.
                    contentAlignment = Alignment.Center // Centra el título.
                ) {
                    Title(
                        text = "¿Cómo te sientes hoy?",
                        textAlign = TextAlign.Center
                    )
                }

                // Calendario emocional.
                // NOTA: Los datos 'sampleDays' son de ejemplo y deben ser reemplazados por datos reales
                //       provenientes de un ViewModel en una implementación completa.
                val sampleDays = remember { // 'remember' para que no se recomponga innecesariamente.
                    // Ejemplo: los primeros 7 días con distintas emociones, el resto vacíos para un mes.
                    listOf(
                        EmotionDay(1, R.drawable.iconhappyimage, onClick = { onDayClick(1) }),
                        EmotionDay(2, R.drawable.iconsadimage,    onClick = { onDayClick(2) }),
                        EmotionDay(3, R.drawable.iconcalmimage,   onClick = { onDayClick(3) }),
                        EmotionDay(4, R.drawable.iconangryimage,  onClick = { onDayClick(4) }),
                        EmotionDay(5, R.drawable.icondisgustimage,onClick = { onDayClick(5) }),
                        EmotionDay(6, R.drawable.iconfearimage,   onClick = { onDayClick(6) }),
                        EmotionDay(7, R.drawable.iconhappyimage,  onClick = { onDayClick(7) })
                    ) + List(28) { EmotionDay(null, null) } // Rellena con días vacíos (ej. hasta 35 celdas).
                }

                CalendarGrid(
                    month = "Abril", // El nombre del mes (debería ser dinámico).
                    days  = sampleDays, // Los datos de los días a mostrar.
                    modifier = Modifier.fillMaxWidth()
                )

                // Enlace "SABER MÁS".
                TextButtonLink(
                    text = "SABER MÁS ?",
                    onClick = onKnowMore, // Acción al pulsar el enlace.
                    modifier = Modifier.align(Alignment.Start) // Alinea a la izquierda.
                )

                // Sección para el estado emocional más repetido.
                Subtitle(text = "Estado emocional más repetido")
                HabitContainer( // Reutiliza HabitContainer para mostrar esta información.
                    icon = {
                        ImageContainer(
                            resId = mostFrequentEmotionRes, // Icono de la emoción.
                            contentDescription = "Emoción más frecuente: ${emotionName(mostFrequentEmotionRes)}",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    // Texto descriptivo que incluye el nombre de la emoción y el número de días.
                    text = "Has estado ${emotionName(mostFrequentEmotionRes)} durante " +
                            "$mostFrequentEmotionDays días de este mes",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {} // Podría navegar a estadísticas detalladas de esa emoción.
                )
            }
        }
    }
}

/**
 * @brief Función helper privada para obtener el nombre de una emoción a partir de su ID de recurso drawable.
 * @param resId El ID del recurso drawable del icono de la emoción.
 * @return Un [String] con el nombre legible de la emoción, o una cadena vacía si no se reconoce el ID.
 */
private fun emotionName(@DrawableRes resId: Int): String = when(resId) {
    R.drawable.iconhappyimage   -> "Feliz"
    R.drawable.iconsadimage     -> "Triste"
    R.drawable.iconcalmimage    -> "Tranquilo"
    R.drawable.iconangryimage   -> "Enojado"
    R.drawable.icondisgustimage -> "Disgusto"
    R.drawable.iconfearimage    -> "Miedo"
    else                         -> "" // Fallback para IDs desconocidos.
}

/**
 * @brief Previsualización Composable para [EmotionalCalentarScreen].
 * @details Muestra la pantalla del calendario emocional con datos de ejemplo.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun PreviewEmotionalCalentarScreen() {
    EmotionalCalentarScreen() // Llama al Composable con sus valores por defecto.
}