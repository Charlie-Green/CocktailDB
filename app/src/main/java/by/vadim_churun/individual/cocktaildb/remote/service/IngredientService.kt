package by.vadim_churun.individual.cocktaildb.remote.service

import by.vadim_churun.individual.cocktaildb.remote.pojo.IngredientsArrayPojo
import retrofit2.Call
import retrofit2.http.*


interface IngredientService {
    @GET("search.php")
    fun search(@Query("i") query: String): Call<IngredientsArrayPojo>
}