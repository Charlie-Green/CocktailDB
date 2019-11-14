package by.vadim_churun.individual.cocktaildb.db.entity

import androidx.room.Embedded


class IngredientInRecipeEntity(
    @Embedded(prefix = "lot_")
    val lot: IngredientLotEntity,

    @Embedded(prefix = "ingredient_")
    val ingredient: IngredientEntity
)