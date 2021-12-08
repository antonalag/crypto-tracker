package edu.uoc.tfm.antonalag.cryptotracker.core.converter

import androidx.room.TypeConverter
import java.util.*

/**
 * Class referring to complex data in room
 */
class BaseConverters {

    /**
     * Convert Timestamp to Date
     */
    @TypeConverter
    fun fromTimestamptoDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Convert Date to Timestamp
     */
    @TypeConverter
    fun fromDateToTimestamp(value: Date?): Long?{
        return value?.time
    }
}