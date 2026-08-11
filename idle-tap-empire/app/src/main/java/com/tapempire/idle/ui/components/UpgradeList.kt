package com.tapempire.idle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapempire.idle.ui.theme.Gold
import com.tapempire.idle.ui.theme.TextPrimary
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.util.NumberFormatter
import com.tapempire.idle.viewmodel.GameUiState
import com.tapempire.idle.viewmodel.GameViewModel
import com.tapempire.idle.viewmodel.UpgradeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradesTab(
    uiState: GameUiState,
    onBuy: (String) -> Unit,
    onCycleBuyAmount: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alım miktarı:", color = TextSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            AssistChip(
                onClick = onCycleBuyAmount,
                label = {
                    Text(if (uiState.buyAmount == GameViewModel.BUY_MAX) "MAKS" else "x${uiState.buyAmount}")
                }
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { SectionHeader("👆 Dokunuş Yükseltmeleri") }
            items(uiState.clickUpgrades, key = { it.def.id }) { upgrade ->
                UpgradeCard(upgrade, onBuy)
            }
            item { SectionHeader("⚙️ Otomatik Üreticiler") }
            items(uiState.generatorUpgrades, key = { it.def.id }) { upgrade ->
                UpgradeCard(upgrade, onBuy)
            }
            item { Spacer(modifier = Modifier.width(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Gold,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun UpgradeCard(state: UpgradeUiState, onBuy: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.def.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(state.def.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(state.def.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(
                    text = "Seviye ${state.level} • Toplam +${NumberFormatter.format(state.currentEffect)}",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onBuy(state.def.id) }, enabled = state.canAfford) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("x${state.buyCount}", fontWeight = FontWeight.Bold)
                    Text(NumberFormatter.format(state.buyCost))
                }
            }
        }
    }
}
