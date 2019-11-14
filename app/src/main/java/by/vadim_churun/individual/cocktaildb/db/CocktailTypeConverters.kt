package by.vadim_churun.individual.cocktaildb.db

import androidx.room.TypeConverter
import by.vadim_churun.individual.cocktaildb.db.entity.*


object CocktailTypeConverters {
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
}