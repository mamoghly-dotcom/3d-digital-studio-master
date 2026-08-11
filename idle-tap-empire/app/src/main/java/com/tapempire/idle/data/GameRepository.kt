package com.tapempire.idle.data

import android.content.Context
import android.content.SharedPreferences

/** Thin SharedPreferences wrapper. All numeric currency values are stored as strings
 *  to avoid Float precision loss on very large idle-game numbers. */
class GameRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    data class SavedState(
        val gold: Double,
        val totalEarned: Double,
        val earnedSincePrestige: Double,
        val totalClicks: Long,
        val diamonds: Int,
        val prestigeCount: Int,
        val lastSaveTime: Long,
        val upgradeLevels: Map<String, Int>,
        val unlockedAchievements: Set<String>
    )

    fun load(): SavedState {
        val upgradeLevels = mutableMapOf<String, Int>()
        val raw = prefs.getString(KEY_UPGRADE_LEVELS, "") ?: ""
        if (raw.isNotEmpty()) {
            raw.split(";").forEach { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val level = parts[1].toIntOrNull() ?: 0
                    if (level > 0) upgradeLevels[parts[0]] = level
                }
            }
        }
        return SavedState(
            gold = prefs.getString(KEY_GOLD, null)?.toDoubleOrNull() ?: 0.0,
            totalEarned = prefs.getString(KEY_TOTAL_EARNED, null)?.toDoubleOrNull() ?: 0.0,
            earnedSincePrestige = prefs.getString(KEY_EARNED_SINCE_PRESTIGE, null)?.toDoubleOrNull() ?: 0.0,
            totalClicks = prefs.getLong(KEY_TOTAL_CLICKS, 0L),
            diamonds = prefs.getInt(KEY_DIAMONDS, 0),
            prestigeCount = prefs.getInt(KEY_PRESTIGE_COUNT, 0),
            lastSaveTime = prefs.getLong(KEY_LAST_SAVE_TIME, System.currentTimeMillis()),
            upgradeLevels = upgradeLevels,
            unlockedAchievements = prefs.getStringSet(KEY_ACHIEVEMENTS, emptySet())?.toSet() ?: emptySet()
        )
    }

    fun save(state: SavedState) {
        prefs.edit()
            .putString(KEY_GOLD, state.gold.toString())
            .putString(KEY_TOTAL_EARNED, state.totalEarned.toString())
            .putString(KEY_EARNED_SINCE_PRESTIGE, state.earnedSincePrestige.toString())
            .putLong(KEY_TOTAL_CLICKS, state.totalClicks)
            .putInt(KEY_DIAMONDS, state.diamonds)
            .putInt(KEY_PRESTIGE_COUNT, state.prestigeCount)
            .putLong(KEY_LAST_SAVE_TIME, state.lastSaveTime)
            .putString(KEY_UPGRADE_LEVELS, state.upgradeLevels.entries.joinToString(";") { "${it.key}:${it.value}" })
            .putStringSet(KEY_ACHIEVEMENTS, state.unlockedAchievements)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "tap_empire_prefs"
        private const val KEY_GOLD = "gold"
        private const val KEY_TOTAL_EARNED = "total_earned"
        private const val KEY_EARNED_SINCE_PRESTIGE = "earned_since_prestige"
        private const val KEY_TOTAL_CLICKS = "total_clicks"
        private const val KEY_DIAMONDS = "diamonds"
        private const val KEY_PRESTIGE_COUNT = "prestige_count"
        private const val KEY_LAST_SAVE_TIME = "last_save_time"
        private const val KEY_UPGRADE_LEVELS = "upgrade_levels"
        private const val KEY_ACHIEVEMENTS = "unlocked_achievements"
    }
}
