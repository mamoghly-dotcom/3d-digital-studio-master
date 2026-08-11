package com.tapempire.idle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tapempire.idle.ui.theme.TextPrimary
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.util.NumberFormatter
import com.tapempire.idle.viewmodel.GameUiState

@Composable
fun StatsTab(uiState: GameUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        StatRow("Toplam Kazanılan Altın", NumberFormatter.format(uiState.totalEarned))
        StatRow("Toplam Dokunuş", NumberFormatter.formatInt(uiState.totalClicks))
        StatRow("Saniye Başına Altın", NumberFormatter.format(uiState.goldPerSecond))
        StatRow("Dokunuş Gücü", NumberFormatter.format(uiState.clickPower))
        StatRow("Elmas", uiState.diamonds.toString())
        StatRow("Yeniden Doğuş Sayısı", uiState.prestigeCount.toString())
        StatRow("Kazanılmış Başarım", "${uiState.achievements.count { it.unlocked }} / ${uiState.achievements.size}")
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
        Text(value, color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
    }
}
