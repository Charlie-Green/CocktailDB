package by.vadim_churun.individual.cocktaildb.ui.recipe

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailDbAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.ItemizedRecipe
import kotlinx.android.synthetic.main.recipe_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecipeFragment: CocktailDbAbstractFragment(R.layout.recipe_fragment) {
    companion object {
        const val ARG_DRINK_ID = "drinkID"
    }

    private fun applyRecipe(recipe: ItemizedRecipe) {
        ctbLayout.title = recipe.drink.name
        tvDateModified.text = super.getString(R.string.date_modified, recipe.dateModified)
        frltIngredients.isVisible = (recipe.ingredientsList != null)
        recipe.ingredientsList?.also { tvIngredients.text = it }
        tvRecipe.text = recipe.drink.recipe
            ?: super.getString(R.string.recipe_unknown)
    }

    private fun applyThumb(thumb: Bitmap) {
        val theme = super.requireActivity().theme
        val typval = TypedValue()
        theme.resolveAttribute(android.R.attr.colorBackground, typval, true)
        val colorBack = typval.data

        imgvThumb.setBackgroundColor(colorBack)
        imgvThumb.setImageBitmap(thumb)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(super.getArguments()?.containsKey(ARG_DRINK_ID) != true)
            throw IllegalArgumentException("Missing argument ARG_DRINK_ID")
        val drinkID = super.getArguments()!!.getInt(ARG_DRINK_ID)

        val owner = super.getViewLifecycleOwner()
        super.viewModel.drinkThumbLD.observe(owner, Observer { thumb ->
            if(thumb?.drinkID == drinkID)
                applyThumb(thumb.image)
        })

        lifecycleScope.launch(Dispatchers.Main) {
            applyRecipe( super.viewModel.getRecipe(drinkID) )
            super.viewModel.requestThumb(drinkID)
        }

        val display = super.requireActivity().windowManager.defaultDisplay
        RecipeToolbarUtils.setMaximumHeight(appbarLayout, display)
        RecipeToolbarUtils.setupOffsetListener(appbarLayout, scrltContent)
    }
}