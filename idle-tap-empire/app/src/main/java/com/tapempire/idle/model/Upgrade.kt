package com.tapempire.idle.model

import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow

enum class UpgradeType {
    CLICK,
    GENERATOR
}

/**
 * Static definition of an upgrade. [level] is stored separately in GameViewModel,
 * these objects never change at runtime.
 */
data class UpgradeDef(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val type: UpgradeType,
    val baseCost: Double,
    val baseEffect: Double,
    val costMultiplier: Double = 1.15
) {
    /** Cost to buy the single level at [level] (0-indexed). */
    fun costForLevel(level: Int): Double = baseCost * costMultiplier.pow(level)

    /** Total cost to buy [count] levels starting from [level]. Closed-form geometric sum. */
    fun bulkCost(level: Int, count: Int): Double {
        if (count <= 0) return 0.0
        val start = costForLevel(level)
        return if (costMultiplier == 1.0) {
            start * count
        } else {
            start * (costMultiplier.pow(count) - 1.0) / (costMultiplier - 1.0)
        }
    }

    /** Maximum number of levels affordable with [gold], starting from [level]. */
    fun maxAffordable(level: Int, gold: Double): Int {
        val start = costForLevel(level)
        if (gold < start) return 0
        if (costMultiplier == 1.0) return floor(gold / start).toInt()
        val n = ln(gold * (costMultiplier - 1.0) / start + 1.0) / ln(costMultiplier)
        return floor(n).toInt().coerceAtLeast(0)
    }
}

object Upgrades {
    val CLICK_UPGRADES = listOf(
        UpgradeDef(
            id = "click_1",
            name = "Güçlü Parmak",
            description = "Her dokunuş için +1 altın",
            icon = "👆",
            type = UpgradeType.CLICK,
            baseCost = 20.0,
            baseEffect = 1.0
        ),
        UpgradeDef(
            id = "click_2",
            name = "Altın Eldiven",
            description = "Her dokunuş için +5 altın",
            icon = "🧤",
            type = UpgradeType.CLICK,
            baseCost = 300.0,
            baseEffect = 5.0
        ),
        UpgradeDef(
            id = "click_3",
            name = "Enerji İçeceği",
            description = "Her dokunuş için +25 altın",
            icon = "🥤",
            type = UpgradeType.CLICK,
            baseCost = 3_500.0,
            baseEffect = 25.0
        ),
        UpgradeDef(
            id = "click_4",
            name = "Robotik El",
            description = "Her dokunuş için +140 altın",
            icon = "🦾",
            type = UpgradeType.CLICK,
            baseCost = 45_000.0,
            baseEffect = 140.0
        ),
        UpgradeDef(
            id = "click_5",
            name = "Kuantum Dokunuş",
            description = "Her dokunuş için +800 altın",
            icon = "✨",
            type = UpgradeType.CLICK,
            baseCost = 600_000.0,
            baseEffect = 800.0
        ),
        UpgradeDef(
            id = "click_6",
            name = "Efsanevi Tıklayıcı",
            description = "Her dokunuş için +5.000 altın",
            icon = "🌟",
            type = UpgradeType.CLICK,
            baseCost = 9_000_000.0,
            baseEffect = 5_000.0
        )
    )

    val GENERATOR_UPGRADES = listOf(
        UpgradeDef(
            id = "gen_1",
            name = "Stajyer",
            description = "Saniyede +0,1 altın üretir",
            icon = "🧑‍💼",
            type = UpgradeType.GENERATOR,
            baseCost = 15.0,
            baseEffect = 0.1
        ),
        UpgradeDef(
            id = "gen_2",
            name = "Serbest Çalışan",
            description = "Saniyede +1 altın üretir",
            icon = "💻",
            type = UpgradeType.GENERATOR,
            baseCost = 100.0,
            baseEffect = 1.0
        ),
        UpgradeDef(
            id = "gen_3",
            name = "Ofis Çalışanı",
            description = "Saniyede +8 altın üretir",
            icon = "🏢",
            type = UpgradeType.GENERATOR,
            baseCost = 1_100.0,
            baseEffect = 8.0
        ),
        UpgradeDef(
            id = "gen_4",
            name = "Satış Ekibi",
            description = "Saniyede +47 altın üretir",
            icon = "📈",
            type = UpgradeType.GENERATOR,
            baseCost = 12_000.0,
            baseEffect = 47.0
        ),
        UpgradeDef(
            id = "gen_5",
            name = "Küçük Fabrika",
            description = "Saniyede +260 altın üretir",
            icon = "🏭",
            type = UpgradeType.GENERATOR,
            baseCost = 130_000.0,
            baseEffect = 260.0
        ),
        UpgradeDef(
            id = "gen_6",
            name = "Bölge Müdürlüğü",
            description = "Saniyede +1.400 altın üretir",
            icon = "🏬",
            type = UpgradeType.GENERATOR,
            baseCost = 1_400_000.0,
            baseEffect = 1_400.0
        ),
        UpgradeDef(
            id = "gen_7",
            name = "Ulusal Şirket",
            description = "Saniyede +7.800 altın üretir",
            icon = "🏦",
            type = UpgradeType.GENERATOR,
            baseCost = 20_000_000.0,
            baseEffect = 7_800.0
        ),
        UpgradeDef(
            id = "gen_8",
            name = "Küresel Holding",
            description = "Saniyede +44.000 altın üretir",
            icon = "🌍",
            type = UpgradeType.GENERATOR,
            baseCost = 330_000_000.0,
            baseEffect = 44_000.0
        ),
        UpgradeDef(
            id = "gen_9",
            name = "Uzay Madenciliği",
            description = "Saniyede +260.000 altın üretir",
            icon = "🛰️",
            type = UpgradeType.GENERATOR,
            baseCost = 5_100_000_000.0,
            baseEffect = 260_000.0
        ),
        UpgradeDef(
            id = "gen_10",
            name = "Galaksi İmparatorluğu",
            description = "Saniyede +1.600.000 altın üretir",
            icon = "🌌",
            type = UpgradeType.GENERATOR,
            baseCost = 75_000_000_000.0,
            baseEffect = 1_600_000.0
        )
    )

    val ALL: List<UpgradeDef> = CLICK_UPGRADES + GENERATOR_UPGRADES

    fun byId(id: String): UpgradeDef? = ALL.firstOrNull { it.id == id }
}
