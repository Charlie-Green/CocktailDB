package by.vadim_churun.individual.cocktaildb.db.dao

import androidx.room.*
import by.vadim_churun.individual.cocktaildb.db.entity.IngredientEntity


@Dao
interface IngredientDAO {
    @Query("select count(*) from Ingredients where name=:name")
    fun countByName(name: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(ingredient: IngredientEntity)
}