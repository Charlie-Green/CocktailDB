package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context
import by.vadim_churun.individual.cocktaildb.db.CocktailDatabase


abstract class CocktailAbstractRepository(protected val appContext: Context) {
    protected val drinkDAO
        get() = CocktailDatabase.get(appContext).drinkDAO

    protected val ingredientDAO
        get() = CocktailDatabase.get(appContext).ingredientDAO
}