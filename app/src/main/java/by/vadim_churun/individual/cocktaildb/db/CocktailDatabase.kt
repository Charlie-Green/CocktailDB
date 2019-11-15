package by.vadim_churun.individual.cocktaildb.db

import android.content.Context
import androidx.room.*
import by.vadim_churun.individual.cocktaildb.db.dao.*
import by.vadim_churun.individual.cocktaildb.db.entity.*


@Database(
    entities = [
        DrinkEntity::class,
        IngredientEntity::class,
        IngredientLotEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CocktailTypeConverters::class)
abstract class CocktailDatabase: RoomDatabase() {
    abstract val drinkDAO: DrinkDAO
    abstract val ingredientDAO: IngredientDAO
    
    
    companion object {
        private var instance: CocktailDatabase? = null

        fun get(appContext: Context)
            = instance ?: synchronized(this) {
                instance ?: Room
                    .databaseBuilder(appContext, CocktailDatabase::class.java, "cocktail")
                    .build()
                    .also { instance = it }
            }
    }
}