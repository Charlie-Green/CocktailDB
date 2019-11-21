package by.vadim_churun.individual.cocktaildb.ui.drink

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkHeaderEntity
import by.vadim_churun.individual.cocktaildb.ui.CocktailAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.DrinksList
import kotlinx.android.synthetic.main.drinks_fragment.*


class DrinksFragment: CocktailAbstractFragment(R.layout.drinks_fragment) {
    private fun applyDrinks(drinks: DrinksList) {
        val newAdapter = DrinksAdapter(super.requireContext(), drinks)
        recvCocktails.layoutManager = recvCocktails.layoutManager ?: run {
            val metrs = DisplayMetrics()
            super.requireActivity().windowManager.defaultDisplay.getMetrics(metrs)
            val cardHeight = super.getResources()
                .getDimensionPixelSize(R.dimen.cocktail_card_height)
            GridLayoutManager(super.requireContext(), metrs.widthPixels / cardHeight)
        }
        recvCocktails.swapAdapter(newAdapter, true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*
        super.viewModel.drinksListLD.observe(super.getViewLifecycleOwner(), Observer { drinks ->
            applyDrinks(drinks)
        })
         */
        listOf(
            DrinkHeaderEntity(0, "Margaritta"),
            DrinkHeaderEntity(1, "Senorita"),
            DrinkHeaderEntity(2, "Whiskey"),
            DrinkHeaderEntity(3, "X-siski")
        ).also {
            applyDrinks( DrinksList(it) )
        }
    }
}