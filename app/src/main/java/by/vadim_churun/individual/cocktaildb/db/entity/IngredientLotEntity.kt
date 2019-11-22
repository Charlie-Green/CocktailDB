package by.vadim_churun.individual.cocktaildb.db.entity

import androidx.room.*


@Entity(
    tableName="IngredientLots",
    primaryKeys = [ "drink", "ingredient" ]
) class IngredientLotEntity(
    @ColumnInfo(name="drink")
    val drinkID: Int,

    @ColumnInfo(name="ingredient")
    val ingredientID: Int,

    val measure: String
)