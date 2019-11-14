package by.vadim_churun.individual.cocktaildb.remote.pojo

import com.google.gson.annotations.SerializedName


class DrinkHeaderPojo(
    @SerializedName("idDrink")
    val ID: Int,

    @SerializedName("strDrink")
    val name: String,

    @SerializedName("strDrinkThumb")
    val thumbURL: String?,

    val strAlcoholic: String?
)