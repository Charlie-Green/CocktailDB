package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context
import android.graphics.Bitmap
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.remote.CocktailApi
import java.io.IOException


class CocktailRepository(appContext: Context): CocktailAbstractRepository(appContext) {
    val drinkHeadersLD
        get() = super.drinkDAO.getHeadersLD()

    fun getThumb(url: String): Bitmap?
        = CocktailApi.getDrinkThumb(url)


    fun sync() {
        val allDrinks = mutableListOf<DrinkEntity>()
        for(letter in 'a'..'z') {
            val data = CocktailApi.searchDrinksByFirstLetter(letter)
                ?: throw IOException("Failed to fetch drinks for letter '$letter': empty response")
            val curDrinks = data.drinks ?: continue
            for(j in 0 until curDrinks.size) {
                val drinkPojo = data.drinks[j]
                allDrinks.add( RepoTransformations.drinkPojoToEntity(drinkPojo) )
            }
        }
        super.drinkDAO.addOrUpdate(allDrinks)
    }
}