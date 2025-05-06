package com.app.tibibalance.ui.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.tibibalance.ui.components.NavBarButton

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            Modifier.fillMaxSize(),
            Arrangement.SpaceEvenly,
            Alignment.CenterVertically
        ) {
            items.forEach { item ->
                NavBarButton(
                    item = item,
                    selected = item.route == selectedRoute,
                    onClick = { onItemClick(item.route) }
                )
            }
        }
    }
}