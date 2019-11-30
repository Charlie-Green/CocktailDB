package by.vadim_churun.individual.cocktaildb.db

import androidx.room.TypeConverter
import java.util.*


class CocktailTypeConverters {
    @TypeConverter
    fun calendarToLong(cal: Calendar?): Long?
        = cal?.timeInMillis

    @TypeConverter
    fun longToCalendar(long: Long?): Calendar?
        = long?.let {
            Calendar.getInstance().apply { timeInMillis = long }
        }
}