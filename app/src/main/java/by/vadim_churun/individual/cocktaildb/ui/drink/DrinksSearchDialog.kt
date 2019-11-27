package by.vadim_churun.individual.cocktaildb.ui.drink

import androidx.appcompat.app.AppCompatActivity
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.search.AbstractSearchDialog
import by.vadim_churun.individual.cocktaildb.vm.CocktailDbViewModel


class DrinksSearchDialog: AbstractSearchDialog() {
    override val message: String
        get() = super.getString(R.string.search_drinks)

    override val searchHint: String
        get() = super.getString(R.string.type_drink_name)

    override fun onQuerySubmitted(query: CharSequence) {
        val vm = CocktailDbViewModel.get(super.requireActivity() as AppCompatActivity)
        vm.searchDrinks(query)
    }
}