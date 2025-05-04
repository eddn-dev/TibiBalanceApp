/*
 * ui/components/ModalInfoDialog.kt
 *
 * Diálogo 3-en-1 ultra-versátil:
 *   • LOADING  – indicador circular + texto opcional
 *   • INFO     – icono (cualquiera) + título / mensaje + 1-2 botones
 *   • EMPTY    – no se muestra (visible = false)
 *
 * Ejemplos de uso
 * ------------------------------------------------------------------
 * // Sólo spinner
 * ModalInfoDialog(visible = true, loading = true)
 *
 * // Mensaje de éxito con botón OK
 * ModalInfoDialog(
 *     visible = true,
 *     icon    = Icons.Default.Check,
 *     message = "¡Guardado!",
 *     primaryButton = DialogButton("Aceptar") { dialogVisible = false }
 * )
 *
 * // Mensaje de error con dos botones
 * ModalInfoDialog(
 *     visible = true,
 *     icon       = Icons.Default.Error,
 *     iconColor  = MaterialTheme.colorScheme.error,
 *     message    = "No se pudo completar la operación",
 *     primaryButton   = DialogButton("Reintentar", ::retry),
 *     secondaryButton = DialogButton("Cancelar") { dialogVisible = false }
 * )
 */

package com.app.tibibalance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/** Botón descriptor para no llenar el parámetro con 3 argumentos sueltos  */
data class DialogButton(
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun ModalInfoDialog(
    visible        : Boolean,             // ¿mostrar el diálogo?
    loading        : Boolean = false,     // muestra spinner + textoLoading
    icon           : ImageVector? = null, // null → sólo texto
    iconColor      : Color = MaterialTheme.colorScheme.onPrimaryContainer,
    iconBgColor    : Color = MaterialTheme.colorScheme.primaryContainer,
    title          : String? = null,
    message        : String? = null,
    /** Texto que acompaña a spinner (por defecto “Procesando…”) */
    textLoading    : String = "Procesando…",
    primaryButton  : DialogButton? = null,
    secondaryButton: DialogButton? = null,
    dismissOnBack  : Boolean = !loading,
    dismissOnClickOutside: Boolean = !loading
) {
    if (!visible) return

    Dialog(
        onDismissRequest = { /* bloqueo condicional */ },
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = dismissOnBack,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Surface(
            shape          = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            when {
                loading -> LoadingContent(textLoading)
                else    -> InfoContent(
                    icon, iconColor, iconBgColor,
                    title, message,
                    primaryButton, secondaryButton
                )
            }
        }
    }
}

/* ---------- layouts internos ---------- */

@Composable
private fun LoadingContent(text: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .widthIn(min = 200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun InfoContent(
    icon          : ImageVector?,
    iconColor     : Color,
    iconBgColor   : Color,
    title         : String?,
    message       : String?,
    primaryButton : DialogButton?,
    secondaryButton: DialogButton?
) {
    Column(
        modifier = Modifier
            .padding(32.dp)
            .widthIn(min = 220.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* icono (opcional) */
        icon?.let {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, contentDescription = null,
                    tint   = iconColor,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        title?.let {
            Text(
                text  = it,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        message?.let {
            Text(
                text  = it,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /* secundario al inicio para estilo Material */
            secondaryButton?.let {
                OutlinedButton(
                    onClick = it.onClick,
                    modifier = Modifier.weight(1f)
                ) { Text(it.text) }
            }
            primaryButton?.let {
                PrimaryButton(
                    text     = it.text,
                    onClick  = it.onClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
