package by.vadim_churun.individual.cocktaildb.db

import androidx.room.TypeConverter
import by.vadim_churun.individual.cocktaildb.db.entity.*
import java.util.*


class CocktailTypeConverters {
    @TypeConverter
    fun stringToIngredientUnit(s: String): IngredientLotEntity.Unit {
        if(s.toLowerCase() != "oz")
            throw IllegalArgumentException("Unknown unit $s")
        return IngredientLotEntity.Unit.OZ
    }

    @TypeConverter
    fun ingredientUnitToString(unit: IngredientLotEntity.Unit)
        = when(unit) {
            IngredientLotEntity.Unit.OZ -> "oz"
        }


    @TypeConverter
    fun calendarToLong(cal: Calendar): Long
        = cal.timeInMillis

    @TypeConverter
    fun longToCalendar(long: Long): Calendar
        = Calendar.getInstance().apply { timeInMillis = long }
}