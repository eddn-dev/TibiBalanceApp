/**
 * @file    HomeScreen.kt
 * @ingroup ui_screens_home // Grupo específico para la pantalla principal/Home
 * @brief   Composable principal para la pantalla de inicio (Home/Dashboard) de la aplicación.
 *
 * @details
 * Esta pantalla actúa como el panel central para el usuario después de iniciar sesión.
 * Presenta un resumen de su actividad y progreso en diferentes métricas de bienestar.
 *
 * Componentes y Secciones Principales:
 * - **Encabezado:** Muestra el título "Resumen de Hoy" y un botón secundario para acceder al "Historial".
 * - **Métricas Diarias:** Presenta tarjetas ([AchievementContainer]) para:
 * - Pasos dados.
 * - Minutos de ejercicio.
 * - Calorías quemadas (Kcal).
 * - Frecuencia cardíaca (bpm).
 * Cada tarjeta muestra un icono, el valor numérico y una barra de progreso indicando el avance hacia una meta (implícita).
 * - **Meta de Pasos:** Una sección dedicada que visualiza el progreso actual de pasos del usuario
 * contra una meta diaria (ej. 8,000 pasos), utilizando una barra de progreso lineal personalizada.
 * - **Actividad Reciente:** Lista las últimas actividades o hábitos registrados por el usuario
 * (e.g., "Caminata rápida", "Yoga"), mostrando detalles como duración y hora, utilizando [HabitContainer].
 *
 * La pantalla utiliza un fondo degradado consistente con otras partes de la aplicación y
 * organiza su contenido en una [Column] desplazable verticalmente. Los datos mostrados
 * son actualmente estáticos/ejemplos y deberían ser proporcionados por un ViewModel
 * en una implementación real.
 *
 * @see AchievementContainer Componente reutilizable para mostrar métricas/logros con progreso.
 * @see HabitContainer Componente reutilizable para mostrar filas de hábitos/actividades.
 * @see ImageContainer Usado internamente por AchievementContainer y HabitContainer para los iconos.
 * @see Title, Description Componentes de texto reutilizables.
 * @see SecondaryButton Botón utilizado para la acción "Historial".
 * @see ProgressBar Componente utilizado dentro de AchievementContainer.
 * @see R.drawable Contiene los recursos de iconos para las métricas y actividades.
 */
package com.app.tibibalance.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.tibibalance.R
import com.app.tibibalance.domain.model.RepeatPreset
import com.app.tibibalance.ui.components.* // Importa componentes base como ImageContainer
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.buttons.SecondaryButton
import com.app.tibibalance.ui.components.containers.AchievementContainer // Import específico
import com.app.tibibalance.ui.components.containers.HabitContainer // Import específico
import com.app.tibibalance.ui.components.texts.Description
import com.app.tibibalance.ui.components.texts.Title

import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.window.DialogProperties

/**
 * @brief Composable que define la interfaz de usuario para la pantalla principal (Home).
 *
 * @details Muestra un resumen de la actividad diaria del usuario, incluyendo métricas
 * de pasos, ejercicio, calorías y ritmo cardíaco, el progreso hacia la meta de pasos,
 * y las actividades recientes. El contenido es desplazable verticalmente y tiene un
 * fondo degradado.
 */
@Composable
fun HomeScreen() {

    /* ───── Gradiente de fondo compartido ───── */
    // Define el pincel de degradado vertical usado como fondo.
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    var showModal by remember { mutableStateOf(false) }

    // Contenedor principal que ocupa toda la pantalla y aplica el fondo.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)                 // Aplica el fondo degradado.
    ) {
        /* Contenido desplazable verticalmente */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())   // Permite el desplazamiento.
                .padding(16.dp), // Padding general del contenido.
            verticalArrangement = Arrangement.spacedBy(24.dp) // Espacio vertical entre secciones.
        ) {

            /* ---------- Encabezado de la pantalla ---------- */
            Row(
                modifier = Modifier.fillMaxWidth(), // Ocupa el ancho.
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre título y botón.
                verticalAlignment = Alignment.CenterVertically // Alinea verticalmente.
            ) {
                // Título de la pantalla.
                Title(
                    text = "Resumen de Hoy",
                    modifier = Modifier.weight(1f), // Permite que el título ocupe el espacio restante.
                    textAlign = TextAlign.Start // Alinea el texto a la izquierda.
                )
                // Botón para navegar al historial (acción pendiente).
                SecondaryButton(
                    text = "Historial",
                    onClick = { /* TODO: Implementar navegación al historial */ },
                    modifier = Modifier
                        .width(120.dp) // Ancho fijo para el botón.
                        .height(40.dp) // Altura fija para el botón.
                )
            }

            /* ---------- Sección de Métricas ---------- */
            // Columna para agrupar las filas de métricas.
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Primera fila de métricas (Pasos y Ejercicio).
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre tarjetas.
                ) {
                    // Tarjeta para Pasos.
                    AchievementContainer(
                        icon = { // Slot para el icono.
                            ImageContainer(
                                resId = R.drawable.iconstepsimage,
                                contentDescription = "Pasos",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = "5,230", // Valor actual (ejemplo).
                        description = "Pasos", // Etiqueta.
                        percent = 65, // Porcentaje de progreso (ejemplo).
                        modifier = Modifier.weight(1f) // Ocupa la mitad del espacio.
                    )
                    // Tarjeta para Minutos de Ejercicio.
                    AchievementContainer(
                        icon = {
                            ImageContainer(
                                resId = R.drawable.iconclockimage,
                                contentDescription = "Minutos de ejercicio",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = "35",
                        description = "min de ejercicio",
                        percent = 70,
                        modifier = Modifier.weight(1f)
                    )
                }
                // Segunda fila de métricas (Calorías y Ritmo Cardíaco).
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tarjeta para Calorías Quemadas.
                    AchievementContainer(
                        icon = {
                            ImageContainer(
                                resId = R.drawable.iconfireimage,
                                contentDescription = "Calorías quemadas",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = "450",
                        description = "Kcal quemadas",
                        percent = 55,
                        modifier = Modifier.weight(1f)
                    )
                    // Tarjeta para Frecuencia Cardíaca.
                    AchievementContainer(
                        icon = {
                            ImageContainer(
                                resId = R.drawable.iconheartimage,
                                contentDescription = "Frecuencia cardiaca",
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = "78",
                        description = "bpm",
                        percent = 78, // Usado para la barra, no necesariamente un % real de meta.
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            /* ---------- Sección Meta de Pasos ---------- */
            Title(text = "Meta de pasos") // Título de la sección.
            // Contenedor Surface para la barra de progreso de pasos.
            Surface(
                shape = RoundedCornerShape(16.dp), // Bordes redondeados.
                color = Color(0xFFF5FBFD), // Color de fondo específico.
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // Altura fija.
            ) {
                // Columna interna para organizar la barra y el texto.
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp), // Padding interno.
                    verticalArrangement = Arrangement.SpaceBetween // Espacio entre barra y texto.
                ) {
                    // Barra de progreso personalizada (fondo gris).
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(
                                color = Color(0xFFE0E0E0), // Color del track.
                                shape = RoundedCornerShape(6.dp) // Bordes redondeados.
                            )
                    ) {
                        // Barra de progreso real (azul).
                        Box(
                            Modifier
                                .fillMaxHeight()
                                // Ancho proporcional al progreso (5230 de 8000).
                                .fillMaxWidth(5230f / 8000f)
                                .background(
                                    color = Color(0xFF3EA8FE), // Color del progreso.
                                    shape = RoundedCornerShape(6.dp)
                                )
                        )
                    }
                    // Texto que muestra el progreso numérico.
                    Description(
                        text = "5,230 / 8,000", // Texto de progreso vs meta.
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center // Texto centrado.
                    )
                }
            }

            /* ---------- Sección Actividad Reciente ---------- */
            Title(text = "Actividad Reciente") // Título de la sección.
            // Columna para listar las actividades recientes.
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Contenedor para la primera actividad.
                HabitContainer(
                    icon = {
                        ImageContainer(
                            resId = R.drawable.iconwalkingimage,
                            contentDescription = "Caminata rápida",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    text = "Caminata rápida • 20 min • 10 a.m.", // Detalles de la actividad.
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO: Implementar navegación a detalle de actividad */ }
                )
                // Contenedor para la segunda actividad.
                HabitContainer(
                    icon = {
                        ImageContainer(
                            resId = R.drawable.iconyogaimage,
                            contentDescription = "Yoga",
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    text = "Yoga • 30 min • 7:30 a.m.",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* TODO: Implementar navegación a detalle de actividad */ }
                )
                // Aquí se añadirían más HabitContainer si hubiera más actividades.
            }






            PrimaryButton(
                text = "MostrarModal",
                onClick = {showModal = true},
                container = Color(0xFF4285F4),
                modifier = Modifier.weight(1f)
            )

        } // Fin Column principal scrollable
        if (showModal) {
            val dummyCharts = listOf<@Composable () -> Unit>(
                {
                    Canvas(Modifier.fillMaxSize().background(Color(0xFF3EA8FE))) {
                        drawCircle(Color.White, style = Stroke(8f))
                    }
                },
                {
                    Canvas(Modifier.fillMaxSize().background(Color(0xFFFE3E3E))) {
                        drawLine(
                            Color.White,
                            start = center.copy(x = 0f, y = size.height),
                            end = center.copy(x = size.width, y = 0f),
                            strokeWidth = 8f
                        )
                    }
                },
                {
                    Canvas(Modifier.fillMaxSize().background(Color(0xFF3EDE3E))) {
                        drawRect(Color.White, style = Stroke(8f))
                    }
                }
            )

            ModalWithTabs(
                onDismissRequest = { showModal = false },
                //properties = DialogProperties(usePlatformDefaultWidth = false),
                tabs = listOf(
                    ModalTabItem("Semanal") {
                        StatsTabContent(
                            graphTitle = "Resumen semanal",
                            charts = dummyCharts,
                            //graphContent = { Spacer(modifier = Modifier.height(180.dp)) },// Altura simulada del gráfico},
                            statsLabel = "Estadísticas semanales",
                            stepsValue = "7,120",
                            caloriesValue = "1,800",
                            exerciseValue = "120 min",
                            emotionLabel = "Feliz",
                            highlightedHabitName = "Caminar diario",
                            highlightedHabitNote = "Llevas 5 dias cumpliendolo",
                            habitIconName = "Book"
                            //highlightedHabitIcon = { iconByName("walking") }
                        )
                    },
                    ModalTabItem("Mensual") {
                        StatsTabContent(
                            graphTitle = "Resumen mensual",
                            //graphContent = { /* futura gráfica */ },
                            charts = dummyCharts,
                            statsLabel = "Estadísticas mensuales",
                            stepsValue = "28,430",
                            caloriesValue = "7,200",
                            exerciseValue = "480 min",
                            emotionLabel = "Motivado",
                            highlightedHabitName = "Yoga",
                            highlightedHabitNote = "26 dias de cumplimiento",
                            habitIconName = "Book"
                            //highlightedHabitIcon = { iconByName("yoga") }
                        )
                    }
                )
            )
        }

    } // Fin Box principal
}