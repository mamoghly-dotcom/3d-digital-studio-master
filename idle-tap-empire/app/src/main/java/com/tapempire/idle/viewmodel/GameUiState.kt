package com.tapempire.idle.viewmodel

import com.tapempire.idle.model.AchievementDef
import com.tapempire.idle.model.UpgradeDef

data class UpgradeUiState(
    val def: UpgradeDef,
    val level: Int,
    val buyCount: Int,
    val buyCost: Double,
    val canAfford: Boolean,
    val currentEffect: Double
)

data class AchievementUiState(
    val def: AchievementDef,
    val unlocked: Boolean
)

data class GameUiState(
    val gold: Double = 0.0,
    val goldPerSecond: Double = 0.0,
    val clickPower: Double = 1.0,
    val diamonds: Int = 0,
    val comboCount: Int = 0,
    val comboMultiplierPercent: Int = 0,
    val buyAmount: Int = 1,
    val clickUpgrades: List<UpgradeUiState> = emptyList(),
    val generatorUpgrades: List<UpgradeUiState> = emptyList(),
    val achievements: List<AchievementUiState> = emptyList(),
    val totalEarned: Double = 0.0,
    val totalClicks: Long = 0,
    val prestigeCount: Int = 0,
    val canPrestige: Boolean = false,
    val prestigeGain: Int = 0,
    val earnedSincePrestige: Double = 0.0,
    val pendingOfflineEarnings: Double? = null,
    val offlineTimeAwaySeconds: Long = 0
)

sealed interface GameEvent {
    data class AchievementUnlocked(val achievement: AchievementDef) : GameEvent
}
