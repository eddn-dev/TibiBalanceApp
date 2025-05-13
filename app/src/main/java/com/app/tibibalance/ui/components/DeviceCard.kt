// DeviceCard.kt
package com.app.tibibalance.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.texts.Subtitle
import com.app.tibibalance.ui.components.texts.Caption
import com.app.tibibalance.ui.components.buttons.PrimaryButton

@SuppressLint("MissingPermission")
@Composable
fun DeviceCard(
    device: BluetoothDevice,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Subtitle(
                text = device.name ?: "Desconocido",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Caption(
                    text = if (device.bondState == BluetoothDevice.BOND_BONDED)
                        "Conectado" else "Desconectado"
                )
                Spacer(Modifier.width(8.dp))
                if (device.bondState == BluetoothDevice.BOND_BONDED) {
                    PrimaryButton(
                        text = "Desconectar",
                        onClick = onDisconnect,
                        modifier = Modifier.height(32.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    PrimaryButton(
                        text = "Quitar",
                        onClick = onRemove,
                        modifier = Modifier.height(32.dp)
                    )
                } else {
                    PrimaryButton(
                        text = "Conectar",
                        onClick = onConnect,
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
    }
}
