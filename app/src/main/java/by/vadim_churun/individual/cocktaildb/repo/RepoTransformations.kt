package by.vadim_churun.individual.cocktaildb.repo

import android.annotation.SuppressLint
import android.util.Log
import java.util.Calendar
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinkPojo
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
}