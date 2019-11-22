package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.db.entity.IngredientEntity
import by.vadim_churun.individual.cocktaildb.db.entity.IngredientLotEntity
import by.vadim_churun.individual.cocktaildb.remote.CocktailApi
import by.vadim_churun.individual.cocktaildb.repo.exception.UnknownMeasureUnitException
import java.io.IOException


class CocktailRepository(appContext: Context): CocktailAbstractRepository(appContext) {
    val drinkHeadersLD
        get() = super.drinkDAO.getHeadersLD()

    fun getThumb(url: String): Bitmap?
        = CocktailApi.getDrinkThumb(url)


    private fun fetchIngredient(ingredientName: String): IngredientEntity {
        val ingredientsPojo = CocktailApi.searchIngredients(ingredientName)
            ?: throw IOException("Can't fetch a list of ingredients")
        val ingredients = ingredientsPojo.ingredients
        if(ingredients == null || ingredients.isEmpty())
            throw IllegalArgumentException("No ingredient named \"$ingredientName\" found")
        return RepoTransformations.ingredientPojoToEntity(ingredients[0])
    }


    fun sync() {
        val knownIngredientNames = HashSet<String>()
        for(ingredientName in super.ingredientDAO.getNames()) {
            knownIngredientNames.add(ingredientName)
        }

        val newIngredients = mutableListOf<IngredientEntity>()
        val allIngredientLots = mutableListOf<IngredientLotEntity>()
        fun getOrFetchIngredient(ingredientName: String): IngredientEntity {
            if(knownIngredientNames.contains(ingredientName))
                return super.ingredientDAO.getByName(ingredientName)[0]
            return fetchIngredient(ingredientName).also { newIngredients.add(it) }
        }
        fun parseIngredientLot(
            drinkID: Int,
            ingredientName: String?,
            strMeasure: String?
        ): IngredientLotEntity? {
            if(ingredientName == null || strMeasure == null)
                return null
            return try {
                RepoTransformations.parseIngredientLot(
                    drinkID, getOrFetchIngredient(ingredientName).ID, strMeasure )
                    .also { allIngredientLots.add(it) }
            } catch(exc: UnknownMeasureUnitException) {
                Log.e("Sync", "Unknown measure unit \"${exc.representation}\"")
                null
            }
        }

        val allDrinks = mutableListOf<DrinkEntity>()
        for(letter in 'a'..'z') {
            val data = CocktailApi.searchDrinksByFirstLetter(letter)
                ?: throw IOException("Failed to fetch drinks for letter '$letter': empty response")
            val curDrinks = data.drinks ?: continue
            for(j in 0 until curDrinks.size) {
                val drinkPojo = data.drinks[j]
                allDrinks.add( RepoTransformations.drinkPojoToEntity(drinkPojo) )
                
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient1,  drinkPojo.measure1)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient2,  drinkPojo.measure2)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient3,  drinkPojo.measure3)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient4,  drinkPojo.measure4)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient5,  drinkPojo.measure5)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient6,  drinkPojo.measure6)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient7,  drinkPojo.measure7)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient8,  drinkPojo.measure8)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient9,  drinkPojo.measure9)  ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient10, drinkPojo.measure10) ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient11, drinkPojo.measure11) ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient12, drinkPojo.measure12) ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient13, drinkPojo.measure13) ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient14, drinkPojo.measure14) ?: continue
                parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient15, drinkPojo.measure15)
            }
        }

        Log.v("Sync", "Total drinks: ${allDrinks.size}. New ingredients: ${newIngredients.size}" )
        super.drinkDAO.addOrUpdate(allDrinks)
        super.ingredientDAO.addOrUpdate(newIngredients)
        super.ingredientDAO.addOrUpdateILots(allIngredientLots)
    }
}