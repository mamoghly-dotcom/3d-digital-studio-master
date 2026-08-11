package com.tapempire.idle.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.weight
import com.tapempire.idle.ui.components.AchievementsTab
import com.tapempire.idle.ui.components.GameTopBar
import com.tapempire.idle.ui.components.OfflineEarningsDialog
import com.tapempire.idle.ui.components.PrestigeDialog
import com.tapempire.idle.ui.components.StatsTab
import com.tapempire.idle.ui.components.TapArea
import com.tapempire.idle.ui.components.UpgradesTab
import com.tapempire.idle.viewmodel.GameEvent
import com.tapempire.idle.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showPrestigeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is GameEvent.AchievementUnlocked -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "${event.achievement.icon} Başarım açıldı: ${event.achievement.title}"
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GameTopBar(
                gold = uiState.gold,
                goldPerSecond = uiState.goldPerSecond,
                diamonds = uiState.diamonds,
                onPrestigeClick = { showPrestigeDialog = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TapArea(
                clickPower = uiState.clickPower,
                comboMultiplierPercent = uiState.comboMultiplierPercent,
                onTap = { viewModel.onTap() },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )

            val tabs = listOf("Yükseltmeler", "Başarımlar", "İstatistikler")
            TabRow(selectedTabIndex = selectedTab, containerColor = MaterialTheme.colorScheme.surface) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1.3f)
            ) {
                when (selectedTab) {
                    0 -> UpgradesTab(
                        uiState = uiState,
                        onBuy = viewModel::buyUpgrade,
                        onCycleBuyAmount = viewModel::cycleBuyAmount
                    )
                    1 -> AchievementsTab(uiState.achievements)
                    else -> StatsTab(uiState)
                }
            }
        }
    }

    uiState.pendingOfflineEarnings?.let { amount ->
        OfflineEarningsDialog(
            amount = amount,
            secondsAway = uiState.offlineTimeAwaySeconds,
            onCollect = { viewModel.collectOfflineEarnings() }
        )
    }

    if (showPrestigeDialog) {
        PrestigeDialog(
            currentGain = uiState.prestigeGain,
            canPrestige = uiState.canPrestige,
            earnedSincePrestige = uiState.earnedSincePrestige,
            onConfirm = {
                viewModel.doPrestige()
                showPrestigeDialog = false
            },
            onDismiss = { showPrestigeDialog = false }
        )
    }
}
