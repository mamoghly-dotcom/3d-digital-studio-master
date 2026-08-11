package com.tapempire.idle.model

/** Snapshot of the fields achievements can check, passed in fresh on every evaluation. */
data class GameSnapshot(
    val totalEarned: Double,
    val totalClicks: Long,
    val diamonds: Int,
    val goldPerSecond: Double,
    val clickPower: Double,
    val prestigeCount: Int,
    val upgradesOwnedCount: Int
)

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val goldReward: Double,
    val condition: (GameSnapshot) -> Boolean
)

object Achievements {
    val ALL: List<AchievementDef> = listOf(
        AchievementDef(
            id = "first_click",
            title = "İlk Adım",
            description = "İlk kez ekrana dokun",
            icon = "👆",
            goldReward = 25.0,
            condition = { it.totalClicks >= 1 }
        ),
        AchievementDef(
            id = "click_100",
            title = "Isınıyoruz",
            description = "100 kez dokun",
            icon = "🔥",
            goldReward = 200.0,
            condition = { it.totalClicks >= 100 }
        ),
        AchievementDef(
            id = "click_1000",
            title = "Parmak Ustası",
            description = "1.000 kez dokun",
            icon = "🥋",
            goldReward = 2_000.0,
            condition = { it.totalClicks >= 1_000 }
        ),
        AchievementDef(
            id = "click_10000",
            title = "Dokunuş Efsanesi",
            description = "10.000 kez dokun",
            icon = "🏅",
            goldReward = 50_000.0,
            condition = { it.totalClicks >= 10_000 }
        ),
        AchievementDef(
            id = "first_upgrade",
            title = "Yatırımcı",
            description = "İlk yükseltmeni satın al",
            icon = "🛒",
            goldReward = 50.0,
            condition = { it.upgradesOwnedCount >= 1 }
        ),
        AchievementDef(
            id = "owned_10",
            title = "Koleksiyoncu",
            description = "Toplamda 10 seviye yükseltme sahibi ol",
            icon = "📦",
            goldReward = 1_000.0,
            condition = { it.upgradesOwnedCount >= 10 }
        ),
        AchievementDef(
            id = "owned_50",
            title = "İmparator",
            description = "Toplamda 50 seviye yükseltme sahibi ol",
            icon = "👑",
            goldReward = 25_000.0,
            condition = { it.upgradesOwnedCount >= 50 }
        ),
        AchievementDef(
            id = "earn_10k",
            title = "Cebi Dolu",
            description = "Toplam 10.000 altın kazan",
            icon = "💰",
            goldReward = 500.0,
            condition = { it.totalEarned >= 10_000.0 }
        ),
        AchievementDef(
            id = "earn_1m",
            title = "Milyoner",
            description = "Toplam 1.000.000 altın kazan",
            icon = "💵",
            goldReward = 50_000.0,
            condition = { it.totalEarned >= 1_000_000.0 }
        ),
        AchievementDef(
            id = "earn_1b",
            title = "Milyarder",
            description = "Toplam 1.000.000.000 altın kazan",
            icon = "🏆",
            goldReward = 2_000_000.0,
            condition = { it.totalEarned >= 1_000_000_000.0 }
        ),
        AchievementDef(
            id = "gps_100",
            title = "Otomasyon",
            description = "Saniyede 100 altın üretim gücüne ulaş",
            icon = "⚙️",
            goldReward = 5_000.0,
            condition = { it.goldPerSecond >= 100.0 }
        ),
        AchievementDef(
            id = "gps_10000",
            title = "Sanayi Devi",
            description = "Saniyede 10.000 altın üretim gücüne ulaş",
            icon = "🏗️",
            goldReward = 200_000.0,
            condition = { it.goldPerSecond >= 10_000.0 }
        ),
        AchievementDef(
            id = "first_prestige",
            title = "Yeniden Doğuş",
            description = "İlk kez yeniden doğ",
            icon = "💎",
            goldReward = 0.0,
            condition = { it.prestigeCount >= 1 }
        ),
        AchievementDef(
            id = "diamonds_10",
            title = "Elmas Avcısı",
            description = "10 elmas biriktir",
            icon = "💠",
            goldReward = 0.0,
            condition = { it.diamonds >= 10 }
        ),
        AchievementDef(
            id = "click_power_1000",
            title = "Kırılmaz Dokunuş",
            description = "Dokunuş gücünü 1.000'e çıkar",
            icon = "💪",
            goldReward = 100_000.0,
            condition = { it.clickPower >= 1_000.0 }
        )
    )

    fun byId(id: String): AchievementDef? = ALL.firstOrNull { it.id == id }
}
