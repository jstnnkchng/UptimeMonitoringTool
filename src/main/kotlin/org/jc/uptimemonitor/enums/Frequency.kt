package org.jc.uptimemonitor.enums

/** Frequency enum class for 'DAILY', 'HOURLY', 'EVERY_15_MINUTES' */
enum class Frequency {
    DAILY,
    EVERY_15_MINUTES,
    HOURLY;

    companion object {
        fun fromString(frequency: String): Frequency {
            return valueOf(frequency)
        }
    }
}