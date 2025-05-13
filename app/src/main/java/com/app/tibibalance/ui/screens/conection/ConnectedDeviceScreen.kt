// src/main/java/com/app/tibibalance/ui/screens/conection/ConnectedDeviceScreen.kt
package com.app.tibibalance.ui.screens.conection

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.tibibalance.ui.components.DeviceCard
import com.app.tibibalance.ui.components.RoundedIconButton
import com.app.tibibalance.ui.components.buttons.PrimaryButton
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Title

/**
 * @file ConnectedDeviceScreen.kt
 * @ingroup ui_screens_connection
 * @brief   Pantalla principal para ver y gestionar dispositivos Bluetooth emparejados y disponibles.
 *
 * @details
 * - Muestra una lista de los dispositivos emparejados.
 * - Permite agregar nuevos dispositivos mediante un diálogo que lista los disponibles.
 * - Si no hay dispositivos emparejados, muestra un mensaje de estado vacío.
 *
 * @param pairedDevices   Lista de dispositivos ya emparejados.
 * @param isDialogOpen    Booleano que controla la visibilidad del diálogo de búsqueda.
 * @param availableDevices Lista de dispositivos detectados para emparejar.
 * @param onConnect       Callback al conectar a un dispositivo.
 * @param onDisconnect    Callback al desconectar un dispositivo.
 * @param onRemove        Callback para eliminar un dispositivo de la lista emparejada.
 * @param onAddClick      Callback al pulsar el botón de “Agregar dispositivo”.
 * @param onDialogDismiss Callback para cerrar el diálogo de búsqueda.
 * @param onPair          Callback al seleccionar un dispositivo disponible para emparejar.
 */
@SuppressLint("MissingPermission")
@Composable
fun ConnectedDeviceScreen(
    pairedDevices: List<BluetoothDevice>,
    isDialogOpen: Boolean,
    availableDevices: List<BluetoothDevice>,
    onConnect: (BluetoothDevice) -> Unit,
    onDisconnect: (BluetoothDevice) -> Unit,
    onRemove: (BluetoothDevice) -> Unit,
    onAddClick: () -> Unit,
    onDialogDismiss: () -> Unit,
    onPair: (BluetoothDevice) -> Unit
) {
    // Degradado de fondo
    val background = Brush.verticalGradient(
        colors = listOf(Color(0xFF3EA8FE).copy(alpha = .25f), Color.White)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(50.dp))

        // Título principal
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFAED3E3)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(40.dp)
        ) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Title(text = "Mis Dispositivos")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Estado vacío o lista de emparejados
        if (pairedDevices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes dispositivos emparejados aún",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pairedDevices) { device ->
                    DeviceCard(
                        device = device,
                        onConnect = { onConnect(device) },
                        onDisconnect = { onDisconnect(device) },
                        onRemove = { onRemove(device) }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botón redondeado de agregar
        RoundedIconButton(
            icon = Icons.Default.Add,
            contentDescription = "Agregar dispositivo",
            onClick = onAddClick,
            modifier = Modifier.padding(bottom = 50.dp)
        )
        Spacer(Modifier.height(15.dp))
    }

    // Diálogo de escaneo
    if (isDialogOpen) {
        Dialog(onDismissRequest = onDialogDismiss) {
            Card(
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Subtitle(text = "Dispositivos Disponibles")
                    Spacer(Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableDevices) { device ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable { onPair(device) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = device.name ?: "Desconocido",
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(
                        text = "Cerrar",
                        onClick = onDialogDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

// ----------------------------------------------------------------
// Previews
// ----------------------------------------------------------------

@Preview(showBackground = true, widthDp = 360, heightDp = 600)
@Composable
fun ConnectedDeviceScreen_Preview_Empty() {
    ConnectedDeviceScreen(
        pairedDevices    = emptyList(),
        isDialogOpen     = false,
        availableDevices = emptyList(),
        onConnect        = {},
        onDisconnect     = {},
        onRemove         = {},
        onAddClick       = {},
        onDialogDismiss  = {},
        onPair           = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ConnectedDeviceScreen_Preview_WithDialog() {
    ConnectedDeviceScreen(
        pairedDevices    = emptyList(),
        isDialogOpen     = true,
        availableDevices = emptyList(),
        onConnect        = {},
        onDisconnect     = {},
        onRemove         = {},
        onAddClick       = {},
        onDialogDismiss  = {},
        onPair           = {}
    )
}
