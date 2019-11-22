package by.vadim_churun.individual.cocktaildb.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import by.vadim_churun.individual.cocktaildb.db.entity.*


@Dao
interface IngredientDAO {
    @Query("select name from Ingredients")
    fun getNames(): List<String>

    @Query("select * from Ingredients where name=:name")
    fun getByName(name: String): List<IngredientEntity>

    @Query("select :drinkID as lot_drink, " +
           "IngredientLots.ingredient as lot_ingredient, " +
           "IngredientLots.amount as lot_amount, " +
           "IngredientLots.unit as lot_unit, " +
           "Ingredients.id as ingredient_id, " +
           "Ingredients.name as ingredient_name " +
           "from Ingredients inner join IngredientLots on Ingredients.id=IngredientLots.ingredient" )
    fun getIngredientsInRecipeLD(drinkID: Int): LiveData< List<IngredientInRecipeEntity> >

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateILots(ilots: List<IngredientLotEntity>)
}