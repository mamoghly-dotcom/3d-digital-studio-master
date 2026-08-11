package com.tapempire.idle.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tapempire.idle.data.GameRepository
import com.tapempire.idle.model.AchievementDef
import com.tapempire.idle.model.Achievements
import com.tapempire.idle.model.GameSnapshot
import com.tapempire.idle.model.UpgradeDef
import com.tapempire.idle.model.Upgrades
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.floor
import kotlin.math.sqrt

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    private var gold = 0.0
    private var totalEarned = 0.0
    private var earnedSincePrestige = 0.0
    private var totalClicks = 0L
    private var diamonds = 0
    private var prestigeCount = 0
    private var lastSaveTime = System.currentTimeMillis()
    private val upgradeLevels = mutableMapOf<String, Int>()
    private val unlockedAchievements = mutableSetOf<String>()

    private var buyAmount = 1
    private var comboCount = 0
    private var lastTapTime = 0L

    private var pendingOfflineEarnings: Double? = null
    private var pendingOfflineSeconds: Long = 0

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GameEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<GameEvent> = _events

    init {
        loadState()
        computeOfflineEarnings()
        refreshUiState()
        startGameLoop()
        startAutoSave()
    }

    private fun loadState() {
        val saved = repository.load()
        gold = saved.gold
        totalEarned = saved.totalEarned
        earnedSincePrestige = saved.earnedSincePrestige
        totalClicks = saved.totalClicks
        diamonds = saved.diamonds
        prestigeCount = saved.prestigeCount
        lastSaveTime = saved.lastSaveTime
        upgradeLevels.clear()
        upgradeLevels.putAll(saved.upgradeLevels)
        unlockedAchievements.clear()
        unlockedAchievements.addAll(saved.unlockedAchievements)
    }

    private fun computeOfflineEarnings() {
        val now = System.currentTimeMillis()
        val elapsedSeconds = ((now - lastSaveTime) / 1000L).coerceAtLeast(0L)
        lastSaveTime = now
        if (elapsedSeconds < MIN_OFFLINE_SECONDS) return
        val gps = currentGoldPerSecond()
        if (gps <= 0.0) return
        val cappedSeconds = elapsedSeconds.coerceAtMost(MAX_OFFLINE_SECONDS)
        val earnings = gps * cappedSeconds * OFFLINE_RATE
        if (earnings > 0.0) {
            pendingOfflineEarnings = earnings
            pendingOfflineSeconds = elapsedSeconds
        }
    }

    fun collectOfflineEarnings() {
        val amount = pendingOfflineEarnings ?: return
        addGold(amount)
        pendingOfflineEarnings = null
        pendingOfflineSeconds = 0
        emitNewAchievements(checkAchievements())
        refreshUiState()
        saveState()
    }

    /** Registers a tap, mutates state and returns the gold gained so the UI can animate it. */
    fun onTap(): Double {
        val now = System.currentTimeMillis()
        comboCount = if (now - lastTapTime <= COMBO_WINDOW_MS) {
            (comboCount + 1).coerceAtMost(MAX_COMBO)
        } else {
            1
        }
        lastTapTime = now
        val comboMultiplier = 1.0 + comboCount * COMBO_STEP
        val gain = currentClickPower() * comboMultiplier
        addGold(gain)
        totalClicks++
        emitNewAchievements(checkAchievements())
        refreshUiState()
        return gain
    }

    fun cycleBuyAmount() {
        buyAmount = when (buyAmount) {
            1 -> 10
            10 -> 100
            100 -> BUY_MAX
            else -> 1
        }
        refreshUiState()
    }

    fun buyUpgrade(id: String) {
        val def = Upgrades.byId(id) ?: return
        val level = upgradeLevels[id] ?: 0
        val desiredCount = if (buyAmount == BUY_MAX) {
            def.maxAffordable(level, gold)
        } else {
            buyAmount
        }
        if (desiredCount <= 0) return
        val cost = def.bulkCost(level, desiredCount)
        if (gold < cost) return
        gold -= cost
        upgradeLevels[id] = level + desiredCount
        emitNewAchievements(checkAchievements())
        refreshUiState()
        saveState()
    }

    fun canPrestige(): Boolean = earnedSincePrestige >= PRESTIGE_MIN_EARNED

    private fun prestigeGainAmount(): Int =
        floor(sqrt(earnedSincePrestige / PRESTIGE_MIN_EARNED)).toInt()

    fun doPrestige() {
        val gain = prestigeGainAmount()
        if (gain <= 0) return
        diamonds += gain
        prestigeCount++
        gold = 0.0
        earnedSincePrestige = 0.0
        upgradeLevels.clear()
        comboCount = 0
        emitNewAchievements(checkAchievements())
        refreshUiState()
        saveState()
    }

    private fun addGold(amount: Double) {
        gold += amount
        totalEarned += amount
        earnedSincePrestige += amount
    }

    private fun currentClickPower(): Double {
        val base = 1.0 + Upgrades.CLICK_UPGRADES.sumOf { (upgradeLevels[it.id] ?: 0) * it.baseEffect }
        return base * prestigeMultiplier()
    }

    private fun currentGoldPerSecond(): Double {
        val base = Upgrades.GENERATOR_UPGRADES.sumOf { (upgradeLevels[it.id] ?: 0) * it.baseEffect }
        return base * prestigeMultiplier()
    }

    private fun prestigeMultiplier(): Double = 1.0 + diamonds * 0.02

    private fun checkAchievements(): List<AchievementDef> {
        val snapshot = GameSnapshot(
            totalEarned = totalEarned,
            totalClicks = totalClicks,
            diamonds = diamonds,
            goldPerSecond = currentGoldPerSecond(),
            clickPower = currentClickPower(),
            prestigeCount = prestigeCount,
            upgradesOwnedCount = upgradeLevels.values.sum()
        )
        val newlyUnlocked = mutableListOf<AchievementDef>()
        for (achievement in Achievements.ALL) {
            if (achievement.id !in unlockedAchievements && achievement.condition(snapshot)) {
                unlockedAchievements.add(achievement.id)
                if (achievement.goldReward > 0.0) addGold(achievement.goldReward)
                newlyUnlocked.add(achievement)
            }
        }
        return newlyUnlocked
    }

    private fun emitNewAchievements(newlyUnlocked: List<AchievementDef>) {
        newlyUnlocked.forEach { _events.tryEmit(GameEvent.AchievementUnlocked(it)) }
    }

    private fun startGameLoop() {
        viewModelScope.launch {
            while (isActive) {
                delay(TICK_INTERVAL_MS)
                tick(TICK_INTERVAL_MS / 1000.0)
            }
        }
    }

    private fun tick(deltaSeconds: Double) {
        val gps = currentGoldPerSecond()
        if (gps > 0.0) addGold(gps * deltaSeconds)
        if (comboCount > 0 && System.currentTimeMillis() - lastTapTime > COMBO_WINDOW_MS) {
            comboCount = 0
        }
        emitNewAchievements(checkAchievements())
        refreshUiState()
    }

    private fun startAutoSave() {
        viewModelScope.launch {
            while (isActive) {
                delay(AUTOSAVE_INTERVAL_MS)
                saveState()
            }
        }
    }

    private fun saveState() {
        lastSaveTime = System.currentTimeMillis()
        repository.save(
            GameRepository.SavedState(
                gold = gold,
                totalEarned = totalEarned,
                earnedSincePrestige = earnedSincePrestige,
                totalClicks = totalClicks,
                diamonds = diamonds,
                prestigeCount = prestigeCount,
                lastSaveTime = lastSaveTime,
                upgradeLevels = upgradeLevels.toMap(),
                unlockedAchievements = unlockedAchievements.toSet()
            )
        )
    }

    private fun buildUpgradeUi(def: UpgradeDef): UpgradeUiState {
        val level = upgradeLevels[def.id] ?: 0
        val count = if (buyAmount == BUY_MAX) {
            def.maxAffordable(level, gold).coerceAtLeast(1)
        } else {
            buyAmount
        }
        val cost = def.bulkCost(level, count)
        return UpgradeUiState(
            def = def,
            level = level,
            buyCount = count,
            buyCost = cost,
            canAfford = gold >= cost,
            currentEffect = level * def.baseEffect
        )
    }

    private fun refreshUiState() {
        _uiState.value = GameUiState(
            gold = gold,
            goldPerSecond = currentGoldPerSecond(),
            clickPower = currentClickPower(),
            diamonds = diamonds,
            comboCount = comboCount,
            comboMultiplierPercent = (comboCount * COMBO_STEP * 100).toInt(),
            buyAmount = buyAmount,
            clickUpgrades = Upgrades.CLICK_UPGRADES.map { buildUpgradeUi(it) },
            generatorUpgrades = Upgrades.GENERATOR_UPGRADES.map { buildUpgradeUi(it) },
            achievements = Achievements.ALL.map { AchievementUiState(it, it.id in unlockedAchievements) },
            totalEarned = totalEarned,
            totalClicks = totalClicks,
            prestigeCount = prestigeCount,
            canPrestige = canPrestige(),
            prestigeGain = prestigeGainAmount(),
            earnedSincePrestige = earnedSincePrestige,
            pendingOfflineEarnings = pendingOfflineEarnings,
            offlineTimeAwaySeconds = pendingOfflineSeconds
        )
    }

    override fun onCleared() {
        saveState()
        super.onCleared()
    }

    companion object {
        private const val TICK_INTERVAL_MS = 100L
        private const val AUTOSAVE_INTERVAL_MS = 5_000L
        private const val COMBO_WINDOW_MS = 1_500L
        private const val COMBO_STEP = 0.02
        private const val MAX_COMBO = 50
        private const val PRESTIGE_MIN_EARNED = 1_000_000.0
        private const val MIN_OFFLINE_SECONDS = 60L
        private const val MAX_OFFLINE_SECONDS = 8L * 3600L
        private const val OFFLINE_RATE = 0.5
        const val BUY_MAX = -1
    }
}
