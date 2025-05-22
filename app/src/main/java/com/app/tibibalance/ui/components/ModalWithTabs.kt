/**
 * @file    ModalWithTabs.kt
 * @ingroup ui_component_modal // Grupo para componentes modales o de diálogo
 * @brief   Define un [Composable] para un diálogo modal que contiene pestañas y contenido paginado.
 *
 * @details Este archivo contiene la data class [ModalTabItem] para definir cada pestaña
 * (título y contenido) y el [Composable] principal `ModalWithTabs`. Este último utiliza
 * el componente base [ModalContainer] para la estructura del diálogo (incluyendo el botón
 * de cierre opcional) y dentro organiza una fila de pestañas ([TabRow]) y un paginador
 * horizontal ([HorizontalPager]) para mostrar el contenido asociado a la pestaña seleccionada.
 *
 * La navegación entre pestañas y páginas está sincronizada mediante un [PagerState].
 * Permite personalizar la apariencia de las pestañas (colores seleccionados/no seleccionados)
 * y asegura que haya espacio adecuado para el botón de cierre si está habilitado,
 * añadiendo padding al final del `TabRow`.
 *
 * Es ideal para presentar información relacionada pero separada en secciones dentro de un modal,
 * como estadísticas semanales/mensuales, configuraciones detalladas, etc.
 *
 * @see ModalContainer Contenedor base utilizado para la ventana del diálogo.
 * @see androidx.compose.material3.TabRow Componente de Material 3 para mostrar las pestañas.
 * @see androidx.compose.material3.Tab Composable para una pestaña individual.
 * @see androidx.compose.foundation.pager.HorizontalPager Componente para paginar el contenido horizontalmente.
 * @see androidx.compose.foundation.pager.PagerState Estado que gestiona la página actual y la animación del Pager.
 * @see ModalTabItem Data class que define la estructura de cada pestaña.
 * @see ExperimentalFoundationApi Necesario para PagerState y HorizontalPager.
 */
package com.app.tibibalance.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState // Import necesario
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.app.tibibalance.R
import com.app.tibibalance.ui.components.buttons.PrimaryButton // Para preview
import com.app.tibibalance.ui.components.texts.Description // Para preview
import com.app.tibibalance.ui.components.texts.Title
import com.app.tibibalance.ui.components.GraphsSlider
import com.app.tibibalance.ui.components.buttons.IconButton
import com.app.tibibalance.ui.components.inputs.iconByName
import com.app.tibibalance.ui.components.texts.Subtitle
import kotlinx.coroutines.CoroutineScope // Import necesario
import kotlinx.coroutines.launch

/**
 * @brief Data class que define el título y el contenido de una pestaña para [ModalWithTabs].
 *
 * @param title El [String] que se mostrará como título de la pestaña en el [TabRow].
 * @param content Un slot [Composable] lambda que define el contenido a mostrar en el
 * [HorizontalPager] cuando esta pestaña esté seleccionada.
 */
data class ModalTabItem(
    val title: String,
    val content: @Composable () -> Unit
)

/**
 * @brief Un contenedor modal que presenta contenido organizado en pestañas horizontales.
 *
 * @details Utiliza [ModalContainer] como base y añade un [TabRow] sincronizado con un
 * [HorizontalPager]. El `TabRow` se ajusta para dejar espacio al botón de cierre
 * si está habilitado. Permite personalizar los colores de las pestañas seleccionadas
 * y no seleccionadas.
 *
 * @param onDismissRequest Lambda que se invoca cuando el usuario intenta cerrar el diálogo
 * (a través del botón 'X' o descartándolo si `properties` lo permite).
 * @param tabs La lista de [ModalTabItem] que define las pestañas y su contenido asociado.
 * Si la lista está vacía, el componente no renderiza nada.
 * @param modifier Modificador opcional para el [ModalContainer].
 * @param shape La [Shape] para el [Card] del [ModalContainer]. Por defecto [RoundedCornerShape] de 16.dp.
 * @param containerColor Color de fondo para el [Card] del [ModalContainer]. Por defecto `MaterialTheme.colorScheme.surface`.
 * @param contentColor Color del contenido principal dentro del [ModalContainer]. Por defecto `contentColorFor(containerColor)`.
 * @param properties [DialogProperties] para configurar el comportamiento del [Dialog] base.
 * @param closeButtonEnabled [Boolean] para mostrar u ocultar el botón de cierre 'X'. Por defecto `true`.
 * @param initialTabIndex El índice de la pestaña que debe estar seleccionada inicialmente. Se ajusta para
 * estar dentro de los límites válidos de la lista `tabs`. Por defecto `0`.
 * @param selectedTabColor El [Color] de fondo para la pestaña [Tab] actualmente seleccionada.
 * Por defecto `MaterialTheme.colorScheme.primaryContainer`.
 * @param unselectedTabColor El [Color] de fondo para las pestañas no seleccionadas y para el [TabRow] en sí.
 * Por defecto `containerColor` (el color de fondo del modal).
 * @param selectedTextColor El [Color] del texto de la pestaña [Tab] actualmente seleccionada.
 * Por defecto `contentColorFor(selectedTabColor)`.
 * @param unselectedTextColor El [Color] del texto de las pestañas no seleccionadas.
 * Por defecto `contentColorFor(unselectedTabColor)` con una transparencia alfa de 0.7f.
 */
@OptIn(ExperimentalFoundationApi::class) // Necesario para PagerState y HorizontalPager
@Composable
fun ModalWithTabs(
    onDismissRequest: () -> Unit,
    tabs: List<ModalTabItem>,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = Color(0xFFDDEDF3),
    contentColor: Color = contentColorFor(containerColor),
    properties: DialogProperties = DialogProperties(),
    closeButtonEnabled: Boolean = true,
    initialTabIndex: Int = 0,
    selectedTabColor: Color = Color(0xFFBBDEFB),
    unselectedTabColor: Color = containerColor,
    selectedTextColor: Color = contentColorFor(selectedTabColor),
    unselectedTextColor: Color = contentColorFor(unselectedTabColor).copy(alpha = 0.7f)
) {
    // No renderizar nada si no hay pestañas que mostrar
    if (tabs.isEmpty()) return

    // Asegura que el índice inicial esté dentro de los límites
    val validInitialIndex = initialTabIndex.coerceIn(0, tabs.lastIndex)
    // Estado para el Pager que controla la página/pestaña actual
    val pagerState = rememberPagerState(initialPage = validInitialIndex) { tabs.size }
    // Scope para lanzar la animación de cambio de página
    val coroutineScope = rememberCoroutineScope()

    // Usa ModalContainer como base para la estructura del diálogo
    ModalContainer(
        onDismissRequest = onDismissRequest,
        modifier = modifier.then(Modifier.fillMaxWidth()), // Pasa el modificador
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        properties = properties,
        closeButtonEnabled = closeButtonEnabled
        // Los colores del botón de cierre se toman de ModalContainer
    ) {
        // Columna principal dentro del modal
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fila de Pestañas
            TabRow(
                selectedTabIndex = pagerState.currentPage, // Pestaña seleccionada según el Pager
                containerColor = unselectedTabColor, // Color de fondo de la barra de pestañas
                contentColor = unselectedTextColor, // Color de texto por defecto para pestañas no seleccionadas
                indicator = {}, // Sin indicador de pestaña (el fondo de la Tab actúa como tal)
                divider = {}, // Sin divisor inferior
                // Añade padding al final solo si el botón de cierre está habilitado
                modifier = Modifier.padding(end = if (closeButtonEnabled) 40.dp else 0.dp)
            ) {
                // Itera sobre los items de pestaña definidos
                tabs.forEachIndexed { index, tabItem ->
                    val selected = pagerState.currentPage == index // Determina si esta pestaña es la actual
                    // Pestaña individual
                    Tab(
                        selected = selected, // Estado de selección
                        onClick = { // Acción al hacer clic en la pestaña
                            // Lanza una corrutina para animar el cambio de página en el Pager
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier
                            .height(48.dp) // Altura fija para las pestañas
                            // Aplica forma redondeada solo en las esquinas superiores
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            // Cambia el fondo según si está seleccionada
                            .background(if (selected) selectedTabColor else unselectedTabColor)
                            // Padding interno de la pestaña
                            .padding(vertical = 2.dp, horizontal = 12.dp),
                        // Contenido de la pestaña (el texto)
                        text = {
                            Text(
                                text = tabItem.title, // Título de la pestaña
                                // Cambia el color del texto según si está seleccionada
                                color = if (selected) selectedTextColor else unselectedTextColor,
                                // Cambia el peso de la fuente según si está seleccionada
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    )
                }
            } // Fin TabRow

            // Paginador Horizontal que muestra el contenido de la pestaña seleccionada
            HorizontalPager(
                state = pagerState, // Vinculado al mismo estado que TabRow
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ocupa el espacio vertical restante
                // userScrollEnabled = false // Opcional: Deshabilitar deslizamiento manual entre páginas
            ) { pageIndex -> // Lambda que define el contenido de cada página
                // Contenedor para el contenido de la página actual
                Box(
                    modifier = Modifier,
                        //.fillMaxSize(), // Ocupa el espacio del Pager
                        //.padding(16.dp), // Padding para el contenido interno
                    contentAlignment = Alignment.TopStart // Alineación por defecto del contenido
                ) {
                    // Renderiza el Composable asociado a la pestaña/página actual
                    tabs[pageIndex].content()
                }
            } // Fin HorizontalPager
        } // Fin Column principal
    } // Fin ModalContainer
}



@Composable
fun StatsTabContent(
    graphTitle: String,
    charts: List<@Composable () -> Unit>,
    //graphContent: @Composable () -> Unit,
    statsLabel: String,
    stepsValue: String,
    caloriesValue: String,
    exerciseValue: String,
    emotionLabel: String,
    highlightedHabitName: String,
    highlightedHabitNote: String,
    habitIconName: String,
    emotionIconName: String
) {
    Column(
        //horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {


        /***************************************************************************************************/

        MaterialTheme {
            GraphsSlider(
                charts = charts,
                pageHeight = 180.dp,
                thumbSize  = 50.dp,
                spacing    = 8.dp
            )
        }


        //graphContent()
        /***************************************************************************************************/
        /*Text(
            text = graphTitle,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )*/

        Spacer(modifier = Modifier.height(12.dp))

        //graphContent()

       // Spacer(modifier = Modifier.height(20.dp))

        FormContainer(
            backgroundColor = Color(0xFFD9D9D9),
            modifier = Modifier
                .fillMaxWidth()
                //.padding(horizontal = 8.dp)
        ) {
            Text(
                text = statsLabel,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Start
            )

           // Spacer(modifier = Modifier.height(2.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                FormContainer(
                    backgroundColor = Color(0xFF95D0BA),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageContainer(
                                resId = R.drawable.ic_exercise,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                           // Column {
                            Text(text = exerciseValue, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ejercicio", fontSize = 12.sp)
                            //}
                        }


                }

                FormContainer(
                    backgroundColor = Color(0xFFF7B074),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImageContainer(
                            resId = R.drawable.ic_fire,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        //Column {
                        Text(text = caloriesValue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Calorías", fontSize = 12.sp)
                       // }
                    }
                }


                FormContainer(
                    backgroundColor = Color(0xFFEEC86F),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ImageContainer(
                            resId = R.drawable.ic_steps,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                       // Column {
                        Text(text = stepsValue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Pasos", fontSize = 12.sp)
                        //}
                    }
                }

                FormContainer(
                    backgroundColor = Color(0xFF95B5C6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ImageContainer(
                            resId = iconVectorByName(emotionIconName),
                            contentDescription = null,
                            modifier = Modifier.size(27.dp)
                        )
                        Column {
                            Text(text = emotionLabel, fontWeight = FontWeight.Bold)
                            Text(text = "Emoción dominante", fontSize = 12.sp)
                        }
                    }


                }

            }
        }


        Spacer(modifier = Modifier.height(25.dp))

        Subtitle(
            text = "Hábito destacado",
            modifier = Modifier,
            textAlign = TextAlign.Start
        )


        Spacer(modifier = Modifier.height(12.dp))


        FormContainer(
            backgroundColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                {
                    IconContainer(
                        icon = iconByName(habitIconName),
                        contentDescription = "prueba",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = highlightedHabitName,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                }

            }
        }




        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = highlightedHabitNote,
            color = Color.DarkGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

    }
}





// --- Preview para ModalWithTabs con Espacio para Botón ---
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true, name = "Modal Con Pestañas (Espacio Botón)", heightDp = 600)
@Composable
private fun ModalWithTabsPreview() {
    MaterialTheme {
        // Datos de ejemplo con los títulos solicitados
        val sampleTabs = remember {
            listOf(
                ModalTabItem("Semanal") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Title(text = "Contenido Semanal", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Description(
                            text = "Aquí va la información semanal.",
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
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
                        MaterialTheme {
                            GraphsSlider(
                                charts = dummyCharts,
                                pageHeight = 180.dp,
                                thumbSize  = 50.dp,
                                spacing    = 8.dp
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))





                        //PrimaryButton(text = "Ver Semana", onClick = {})
                    }
                },
                ModalTabItem("Mensual") {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Title(text = "Contenido Mensual", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var checked by remember { mutableStateOf(true) }
                            Description(text = "Activar Resumen:")
                            Spacer(modifier = Modifier.width(8.dp))
                            // Asumiendo que SwitchToggle es un Composable existente
                            Switch(checked = checked, onCheckedChange = { checked = it })
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Description(
                            text = "Resumen detallado del mes.",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
        // Colores y forma para la preview
        val previewContainerColor = Color(0xFFE3F2FD)
        val previewSelectedTabColor = Color(0xFFBBDEFB)
        val previewUnselectedTabColor = previewContainerColor
        val previewSelectedTextColor = Color.Black
        val previewUnselectedTextColor = Color.Black.copy(alpha = 0.6f)

        // Simula el estado visible para la preview
        var visible by remember { mutableStateOf(true) }

        if (visible) {
            ModalWithTabs(
                onDismissRequest = { visible = false }, // Cierra el modal en la preview
                tabs = sampleTabs,
                containerColor = previewContainerColor,
                selectedTabColor = previewSelectedTabColor,
                unselectedTabColor = previewUnselectedTabColor,
                selectedTextColor = previewSelectedTextColor,
                unselectedTextColor = previewUnselectedTextColor,
                closeButtonEnabled = true // Mostrar el botón 'X' en la preview
                // Se puede usar initialTabIndex para probar diferentes pestañas iniciales
            )
        } else {
            // Muestra un botón para reabrir el diálogo en la preview
            Button(onClick = { visible = true }) { Text("Mostrar Modal con Pestañas") }
        }
    }
}


fun iconVectorByName(name: String): Int  {
    return when (name) {
        "iconangryimage" -> R.drawable.iconangryimage
        "iconfearimage" -> R.drawable.iconfearimage
        "icondisgustimage" -> R.drawable.icondisgustimage
        "iconhappyimage" -> R.drawable.iconhappyimage
        "iconsadimage" -> R.drawable.iconsadimage
        else -> R.drawable.iconcalmimage
    }
}
