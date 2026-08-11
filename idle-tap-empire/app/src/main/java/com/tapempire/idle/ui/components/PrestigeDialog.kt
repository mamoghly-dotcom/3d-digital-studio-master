package com.tapempire.idle.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.util.NumberFormatter

@Composable
fun PrestigeDialog(
    currentGain: Int,
    canPrestige: Boolean,
    earnedSincePrestige: Double,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeniden Doğuş 💎") },
        text = {
            Column {
                Text("Tüm altınını ve yükseltmelerini sıfırlayıp $currentGain elmas kazanacaksın.")
                Text("Her elmas, tüm altın kazancına kalıcı olarak %2 bonus verir.")
                if (!canPrestige) {
                    Text(
                        text = "En az 1 elmas kazanmak için 1.000.000 altın kazanman gerekiyor. " +
                            "Şu an: ${NumberFormatter.format(earnedSincePrestige)}",
                        color = TextSecondary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = canPrestige) { Text("Onayla") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
