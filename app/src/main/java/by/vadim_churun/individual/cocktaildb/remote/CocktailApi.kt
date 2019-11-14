package by.vadim_churun.individual.cocktaildb.remote

import by.vadim_churun.individual.cocktaildb.remote.client.CocktailClient
import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinksArrayPojo
import by.vadim_churun.individual.cocktaildb.remote.service.DrinkService


object CocktailApi {
    private val drinkService
        get() = CocktailClient.retrofit.create(DrinkService::class.java)


    fun searchDrinks(query: String): DrinksArrayPojo?
        = this.drinkService.search(query).execute().body()

    fun searchDrinksByFirstLetter(letter: Char): DrinksArrayPojo?
        = this.drinkService.searchByFirstLetter(letter.toString()).execute().body()
}