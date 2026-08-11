package com.tapempire.idle.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.tapempire.idle.util.NumberFormatter

@Composable
fun OfflineEarningsDialog(
    amount: Double,
    secondsAway: Long,
    onCollect: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCollect,
        title = { Text("Hoş geldin! 👋") },
        text = {
            Text(
                "Yokken (${NumberFormatter.formatDuration(secondsAway)}) boyunca " +
                    "${NumberFormatter.format(amount)} altın biriktirdin!"
            )
        },
        confirmButton = {
            TextButton(onClick = onCollect) { Text("Topla") }
        }
    )
}
