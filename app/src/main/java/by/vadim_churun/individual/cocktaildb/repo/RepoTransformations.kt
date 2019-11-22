package by.vadim_churun.individual.cocktaildb.repo

import android.annotation.SuppressLint
import android.util.Log
import java.util.Calendar
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.db.entity.IngredientEntity
import by.vadim_churun.individual.cocktaildb.db.entity.IngredientLotEntity
import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinkPojo
import by.vadim_churun.individual.cocktaildb.remote.pojo.IngredientPojo
import by.vadim_churun.individual.cocktaildb.repo.exception.UnknownMeasureUnitException
import java.text.SimpleDateFormat


object RepoTransformations {
    @SuppressLint("SimpleDateFormat")    // This is the format provided by the API.
    private val DRINK_DATE_MODIFIED_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    fun drinkPojoToEntity(pojo: DrinkPojo)
        = DrinkEntity(
            pojo.ID,
            pojo.name,
            pojo.thumbURL,
            pojo.dateModified?.let {
                try {
                    val parsedTime = DRINK_DATE_MODIFIED_FORMAT.parse(it)
                    Calendar.getInstance().apply { time = parsedTime }
                } catch(exc: Exception) {
                    Log.w("RepoTransformations", "Date not parsed: ${exc.message}")
                    null
                }
            },
            pojo.recipe
        )

    fun ingredientPojoToEntity(pojo: IngredientPojo)
        = IngredientEntity(pojo.ID, pojo.name)


    fun parseIngredientLot
    (drinkID: Int, ingredientID: Int, strMeasure: String): IngredientLotEntity {
        val words = strMeasure.split(' ').filter { it.isNotEmpty() }
        if(words.size != 2)
            throw IllegalArgumentException(
                "Cannot parse strMeasure: Found ${words.size} words instead of 2" )
        return IngredientLotEntity(
            drinkID,
            ingredientID,
            words[0].toFloat(),
            when(words[1].toLowerCase()) {
                "oz" -> IngredientLotEntity.Unit.OZ
                else -> throw UnknownMeasureUnitException(words[1])
            }
        )
    }
}