package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.remote.CocktailApi
import java.io.IOException


class CocktailRepository(appContext: Context): CocktailAbstractRepository(appContext) {
    val drinkHeadersLD
        get() = super.drinkDAO.getHeadersLD()


    /** @return The number of new drinks fetched. **/
    fun sync(): Int {
        val dao = super.drinkDAO
        val newDrinks = mutableListOf<DrinkEntity>()
        for(letter in 'a'..'z') {
            val data = CocktailApi.searchDrinksByFirstLetter(letter)
                ?: throw IOException("Failed to fetch drinks for letter '$letter': empty response")
            for(j in 0 until data.drinks.size) {
                val drinkPojo = data.drinks[j]
                if(dao.count(drinkPojo.ID) == 0)
                    newDrinks.add( RepoTransformations.drinkPojoToEntity(drinkPojo) )
            }
            super.drinkDAO.addOrUpdate(newDrinks)
        }
        return newDrinks.size
    }
}