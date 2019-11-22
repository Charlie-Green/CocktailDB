package by.vadim_churun.individual.cocktaildb.remote

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import by.vadim_churun.individual.cocktaildb.remote.client.CocktailClient
import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinkPojo
import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinksArrayPojo
import by.vadim_churun.individual.cocktaildb.remote.pojo.IngredientsArrayPojo
import by.vadim_churun.individual.cocktaildb.remote.service.DrinkService
import by.vadim_churun.individual.cocktaildb.remote.service.IngredientService
import java.net.URL


object CocktailApi {
    private val drinkService
        get() = CocktailClient.retrofit.create(DrinkService::class.java)

    private val ingredientService
        get() = CocktailClient.retrofit.create(IngredientService::class.java)


    fun searchDrinks(query: String): DrinksArrayPojo?
        = this.drinkService.search(query).execute().body()

    fun searchDrinksByFirstLetter(letter: Char): DrinksArrayPojo?
        = this.drinkService.searchByFirstLetter(letter.toString()).execute().body()

    fun searchIngredients(query: String): IngredientsArrayPojo?
        = this.ingredientService.search(query).execute().body()

    fun getDrinkThumb(host: String): Bitmap? {
        // Retrofit doesn't help much at fetching binary data, so use this legacy approach.
        return try {
            URL(host).openConnection().getInputStream().use { instream ->
                BitmapFactory.decodeStream(instream)
            }
        } catch(exc: Exception) {
            null
        }
    }
}