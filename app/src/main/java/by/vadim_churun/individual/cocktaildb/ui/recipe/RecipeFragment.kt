package by.vadim_churun.individual.cocktaildb.ui.recipe

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.ItemizedRecipe
import kotlinx.android.synthetic.main.recipe_fragment.*


class RecipeFragment: CocktailAbstractFragment(R.layout.recipe_fragment) {
    private fun applyRecipe(recipe: ItemizedRecipe) {
        tvIngredients.text = recipe.ingredientsList
        tvRecipe.text = recipe.instructions
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
        val drinkID = TODO("Get as an argument")
        // TODO: Observe the ItemizedRecipe LiveData.
        // TODO: Observe the DrinkThumb LiveData.
    }
}