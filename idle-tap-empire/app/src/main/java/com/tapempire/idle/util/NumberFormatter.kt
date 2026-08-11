package com.tapempire.idle.util

import java.util.Locale
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/** Formats large idle-game numbers with short suffixes, e.g. 1_500_000.0 -> "1.50M". */
object NumberFormatter {

    private val SUFFIXES = listOf(
        "", "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "No",
        "Dc", "Ud", "Dd", "Td", "Qad", "Qid", "Sxd", "Spd", "Ocd", "Nod"
    )

    fun format(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        val absValue = kotlin.math.abs(value)
        if (absValue < 1000.0) {
            return if (absValue < 10.0 && absValue != floor(absValue)) {
                String.format(Locale.US, "%.1f", value)
            } else {
                floor(value).toLong().toString()
            }
        }

        val magnitude = floor(log10(absValue) / 3.0).toInt().coerceAtMost(SUFFIXES.size - 1)
        if (magnitude <= 0) return floor(value).toLong().toString()

        val scaled = value / 1000.0.pow(magnitude)
        val suffix = SUFFIXES[magnitude]
        return String.format(Locale.US, "%.2f%s", scaled, suffix)
    }

    fun formatInt(value: Long): String = String.format(Locale.US, "%,d", value).replace(',', '.')

    fun formatDuration(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return when {
            hours > 0 -> String.format(Locale.US, "%d sa %d dk", hours, minutes)
            minutes > 0 -> String.format(Locale.US, "%d dk %d sn", minutes, seconds)
            else -> String.format(Locale.US, "%d sn", seconds)
        }
    }
}
