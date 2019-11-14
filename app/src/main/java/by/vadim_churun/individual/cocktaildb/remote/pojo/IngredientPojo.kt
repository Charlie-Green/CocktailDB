package by.vadim_churun.individual.cocktaildb.remote.pojo

import com.google.gson.annotations.SerializedName


class IngredientPojo(
    @SerializedName("idIngredient")
    val ID: Int,

    @SerializedName("strIngredient")
    val name: String
)