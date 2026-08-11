package com.tapempire.idle.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tapempire.idle.ui.theme.Gold
import com.tapempire.idle.ui.theme.LockedGray
import com.tapempire.idle.ui.theme.SuccessGreen
import com.tapempire.idle.ui.theme.TextSecondary
import com.tapempire.idle.viewmodel.AchievementUiState

@Composable
fun AchievementsTab(achievements: List<AchievementUiState>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(achievements, key = { it.def.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.unlocked) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        LockedGray.copy(alpha = 0.25f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (item.unlocked) item.def.icon else "🔒", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.def.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (item.unlocked) Gold else TextSecondary
                        )
                        Text(
                            text = item.def.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                    if (item.unlocked) {
                        Text("✓", color = SuccessGreen, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}
