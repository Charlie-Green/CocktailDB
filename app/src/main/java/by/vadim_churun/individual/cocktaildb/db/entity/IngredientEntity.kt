package by.vadim_churun.individual.cocktaildb.db.entity

import androidx.room.*


@Entity(tableName = "Ingredients")
class IngredientEntity(
    @PrimaryKey @ColumnInfo(name = "id")
    val ID: Int,

    @ColumnInfo(index = true)
    val name: String
)