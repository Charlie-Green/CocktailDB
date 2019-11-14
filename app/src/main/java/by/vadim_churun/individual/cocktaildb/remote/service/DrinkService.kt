package by.vadim_churun.individual.cocktaildb.remote.service

import by.vadim_churun.individual.cocktaildb.remote.pojo.DrinksArrayPojo
import retrofit2.Call
import retrofit2.http.*


interface DrinkService {
    @GET("search.php")
    fun search(@Query("s") query: String): Call<DrinksArrayPojo>

    @GET("search.php")
    fun searchByFirstLetter(@Query("f") letter: String): Call<DrinksArrayPojo>
}