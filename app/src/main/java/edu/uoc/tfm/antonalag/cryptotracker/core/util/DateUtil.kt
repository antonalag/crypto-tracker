package edu.uoc.tfm.antonalag.cryptotracker.core.util

import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

object DateUtil {

    fun fromTimestampToLocalDate(ts: Timestamp): LocalDate{
        return ts.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun fromStringToTimestamp(s: String): Timestamp {
        return Timestamp.valueOf(s)
    }

    fun fromStringToLocalDate(s: String): LocalDate {
        return fromTimestampToLocalDate(fromStringToTimestamp(s))
    }

    fun getTimeOfDayMessage(): String {
        val calendar = Calendar.getInstance()

        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                "Buenos días \uD83C\uDF1E, "
            }
            in 12..20 -> {
                "Buenas tardes \uD83C\uDF1E, "
            }
            in 21..23 -> {
                "Buenas noches \uD83C\uDF1B, "
            }
            else -> "Bienvenido, "
        }
    }

    fun dateNow(): Date {
        return Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

}