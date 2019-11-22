package by.vadim_churun.individual.cocktaildb.repo

import android.annotation.SuppressLint
import android.util.Log
import java.util.Calendar
import by.vadim_churun.individual.cocktaildb.db.entity.*
import by.vadim_churun.individual.cocktaildb.remote.pojo.*
import by.vadim_churun.individual.cocktaildb.repo.exception.UnknownMeasureUnitException
import java.lang.NumberFormatException
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
}