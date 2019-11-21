package by.vadim_churun.individual.cocktaildb.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import by.vadim_churun.individual.cocktaildb.db.entity.*


@Dao
interface DrinkDAO {
    @Query("select id, name from Drinks order by dateModified desc")
    fun getHeadersLD(): LiveData< List<DrinkHeaderEntity> >

    @Query("select * from Drinks where id=:id")
    fun get(id: Int): DrinkEntity

    @Query("select :drinkID as lot_drink, " +
           "IngredientLots.ingredient as lot_ingredient, " +
           "IngredientLots.amount as lot_amount, " +
           "IngredientLots.unit as lot_unit, " +
           "Ingredients.id as ingredient_id, " +
           "Ingredients.name as ingredient_name " +
           "from Ingredients inner join IngredientLots on Ingredients.id=IngredientLots.ingredient" )
    fun getIngredientsInRecipeLD(drinkID: Int): LiveData< List<IngredientInRecipeEntity> >

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(drink: DrinkEntity)
}