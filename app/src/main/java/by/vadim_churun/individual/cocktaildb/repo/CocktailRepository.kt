package by.vadim_churun.individual.cocktaildb.repo

import android.content.Context


class CocktailRepository(appContext: Context): CocktailAbstractRepository(appContext) {
    val drinkHeadersLD
        get() = super.drinkDAO.getHeadersLD()


}