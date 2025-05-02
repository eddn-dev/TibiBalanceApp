package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomNavItem(val label: String, val icon: ImageVector) {
    Emociones("Emociones", Icons.Default.DateRange),
    Habitos("Hábitos", Icons.Default.List),
    Inicio("Inicio", Icons.Default.Home),
    Perfil("Perfil", Icons.Default.Person),
    Ajustes("Ajustes", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    selected: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEDECEF))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem.values().forEach { item ->
            val isSelected = item == selected
            Column(
                modifier = Modifier
                    .clickable { onItemSelected(item) }
                    .background(
                        if (isSelected) Color(0xFFCAC4D0) else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = Color.Black
                )
                Text(
                    text = item.label,
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    var selectedItem by remember { mutableStateOf(BottomNavItem.Inicio) }

    BottomNavBar(selected = selectedItem, onItemSelected = { selectedItem = it })
}