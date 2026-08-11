package com.tapempire.idle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tapempire.idle.ui.theme.DeepPurple
import com.tapempire.idle.ui.theme.Diamond
import com.tapempire.idle.ui.theme.Gold
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.util.NumberFormatter

@Composable
fun GameTopBar(
    gold: Double,
    goldPerSecond: Double,
    diamonds: Int,
    onPrestigeClick: () -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "💰 ${NumberFormatter.format(gold)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Gold
                )
                Text(
                    text = "+${NumberFormatter.format(goldPerSecond)}/sn",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "💎 $diamonds",
                    style = MaterialTheme.typography.titleMedium,
                    color = Diamond
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onPrestigeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Diamond, contentColor = DeepPurple)
                ) {
                    Text("Yeniden Doğ")
                }
            }
        }
    }
}
