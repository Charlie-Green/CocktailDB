package by.vadim_churun.individual.cocktaildb.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(
    tableName="IngredientLots",
    primaryKeys = [ "drink, ingredient" ]
) class IngredientLotEntity(
    @ColumnInfo(name="drink")
    val drinkID: Int,

    @ColumnInfo(name="ingredient")
    val ingredientID: Int,

    val amount: Float,

    val unit: IngredientLotEntity.Unit
) {
    enum class Unit {
        OZ
    }
}