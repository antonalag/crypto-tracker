package edu.uoc.tfm.antonalag.cryptotracker.core.converter

import androidx.room.TypeConverter
import java.util.*

class BaseConverters {

    @TypeConverter
    fun fromTimestamptoDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimestamp(value: Date?): Long?{
        return value?.time
    }
}