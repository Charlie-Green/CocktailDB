package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import by.vadim_churun.individual.cocktaildb.db.entity.*
import by.vadim_churun.individual.cocktaildb.remote.CocktailApi
import java.io.IOException
import java.util.*
import kotlin.collections.HashSet


class CocktailRepository(appContext: Context): CocktailAbstractRepository(appContext) {
    /////////////////////////////////////////////////////////////////////////////////////////
    // WRAPPER:

    val drinkHeadersLD
        get() = super.drinkDAO.getHeadersLD()

    fun getThumb(url: String): Bitmap?
        = CocktailApi.getDrinkThumb(url)

    fun getRecipe(drinkID: Int)
        = RecipeEntity(
            super.drinkDAO.get(drinkID),
            super.ingredientDAO.getIngredientsInRecipe(drinkID)
        )


    /////////////////////////////////////////////////////////////////////////////////////////
    // HELP:

    private fun fetchIngredient(ingredientName: String): IngredientEntity {
        fun throwNoIngredientFound(): Nothing
            = throw IllegalArgumentException("No ingredient named \"$ingredientName\" found")

        val ingredientsPojo = CocktailApi.searchIngredients(ingredientName)
            ?: throw IOException("Can't fetch a list of ingredients")
        val ingredients = ingredientsPojo.ingredients
        if(ingredients == null || ingredients.isEmpty())
            throwNoIngredientFound()

        val nameLower = ingredientName.toLowerCase()
        val ingr = ingredients.find { pojo ->
            pojo.name.toLowerCase() == nameLower
        } ?: throwNoIngredientFound()
        return RepoTransformations.ingredientPojoToEntity(ingr)
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // SYNC:

    fun sync() {
        val knownIngredientNames = HashSet<String>()
        for(ingredientName in super.ingredientDAO.getNames()) {
            knownIngredientNames.add(ingredientName.toLowerCase())
        }

        var quickFlushFlag = (super.drinkDAO.count() < 16)
        val allDrinks = mutableListOf<DrinkEntity>()
        val deletedDrinks = mutableListOf<DrinkEntity>()
        val newIngredients = mutableListOf<IngredientEntity>()
        val allIngredientLots = mutableListOf<IngredientLotEntity>()

        fun getOrFetchIngredient(ingredientName: String): IngredientEntity? {
            if(knownIngredientNames.contains(ingredientName.toLowerCase()))
                return super.ingredientDAO.getByName(ingredientName)[0]
            return try {
                fetchIngredient(ingredientName).also { newIngredients.add(it) }
            } catch(exc: Exception) {
                Log.e("Sync", "No such ingredient \"$ingredientName\"")
                null
            }
        }
        /** @return false if no ingredient with such name was found. If such IngredientLot
          * is added to the database, it will imply fail of forward key constraint (no such ingredient).
          * If it's not, the recipe will be uncompleted (not all ingredients will be mentioned).
          * Thus, if this method returns false, the whole drink should be deleted. **/
        fun parseIngredientLot(
            drinkID: Int,
            ingredientName: String?,
            measure: String?
        ): Boolean {
            if(ingredientName == null || measure == null)
                return true
            val ingredient = getOrFetchIngredient(ingredientName) ?: return false
            allIngredientLots.add(
                IngredientLotEntity(drinkID, ingredient.ID, measure) )
            return  true
        }
        fun flushData() {
            super.drinkDAO.delete(deletedDrinks)
            super.drinkDAO.addOrUpdate(allDrinks)
            super.ingredientDAO.addOrUpdate(newIngredients)
            super.ingredientDAO.addOrUpdateILots(allIngredientLots)
        }

        for(letter in 'a'..'z') {
            val data = CocktailApi.searchDrinksByFirstLetter(letter)
                ?: throw IOException("Failed to fetch drinks for letter '$letter': empty response")
            val curDrinks = data.drinks ?: continue
            for(j in 0 until curDrinks.size) {
                val drinkPojo = data.drinks[j]

                var success =
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient1,  drinkPojo.measure1)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient2,  drinkPojo.measure2)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient3,  drinkPojo.measure3)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient4,  drinkPojo.measure4)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient5,  drinkPojo.measure5)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient6,  drinkPojo.measure6)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient7,  drinkPojo.measure7)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient8,  drinkPojo.measure8)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient9,  drinkPojo.measure9)  &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient10, drinkPojo.measure10) &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient11, drinkPojo.measure11) &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient12, drinkPojo.measure12) &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient13, drinkPojo.measure13) &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient14, drinkPojo.measure14) &&
                    parseIngredientLot(drinkPojo.ID, drinkPojo.ingredient15, drinkPojo.measure15)

                val drinkEntity = RepoTransformations.drinkPojoToEntity(drinkPojo)
                if(success)
                    allDrinks.add(drinkEntity)
                else
                    deletedDrinks.add(drinkEntity)
            }

            if(quickFlushFlag && newIngredients.size >= 16) {
                // Flush a bit of data right now so that the user doesn't stuck with an empty screen.
                flushData()
                deletedDrinks.clear(); allDrinks.clear()
                allIngredientLots.clear(); newIngredients.clear()
                quickFlushFlag = false
            }
        }

        flushData()
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // SEARCH:

    fun filterDrinks(list: List<DrinkHeaderEntity>, query: CharSequence):
    List<DrinkHeaderEntity> {
        android.util.Log.v("Search", "Searching ${list.size} drinks for \"$query\"")
        val subname = query.toString().toLowerCase(Locale.getDefault())
        return list.filter { header ->
            header.name.toLowerCase(Locale.getDefault()).contains(subname)
        }
    }
}