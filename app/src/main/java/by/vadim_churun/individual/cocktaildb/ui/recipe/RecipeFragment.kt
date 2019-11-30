package by.vadim_churun.individual.cocktaildb.ui.recipe

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailDbAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.ItemizedRecipe
import kotlinx.android.synthetic.main.recipe_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class RecipeFragment: CocktailDbAbstractFragment(R.layout.recipe_fragment) {
    /////////////////////////////////////////////////////////////////////////////////////////
    // COMPANION:

    companion object {
        const val ARG_DRINK_ID = "drinkID"
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // UI:

    private fun applyRecipe(recipe: ItemizedRecipe) {
        tvNameLand?.text = recipe.drink.name    // Landscape, toolbar expanded.
        ctbLayout.title = recipe.drink.name     // Other cases.

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

    private fun inflateTransition(onTransitionEnd: () -> Unit): Transition {
        val tr = TransitionInflater
            .from(super.requireContext())
            .inflateTransition(android.R.transition.move)
        tr.duration = 700

        tr.addListener(object: Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition)  {   }
            override fun onTransitionPause(transition: Transition)  {   }
            override fun onTransitionResume(transition: Transition) {   }
            override fun onTransitionEnd(transition: Transition) = onTransitionEnd()
            override fun onTransitionCancel(transition: Transition) {   }
        })

        return tr
    }


    /////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE:

    override fun onCreateView
    (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if(super.getArguments()?.containsKey(ARG_DRINK_ID) != true)
            throw Exception("Missing argument ARG_DRINK_ID")
        val drinkID = super.getArguments()!!.getInt(ARG_DRINK_ID)

        val initUiProcedure = {
            lifecycleScope.launch(Dispatchers.Main) {
                applyRecipe( super.viewModel.getRecipe(drinkID) )
                super.viewModel.requestThumb(drinkID)
            }

            val owner = super.getViewLifecycleOwner()
            super.viewModel.drinkThumbLD.observe(owner, Observer { thumb ->
                if(thumb?.drinkID == drinkID)
                    applyThumb(thumb.image)
            })
        }

        if(savedInstanceState == null) {
            // The user came here from DrinksFragment. Animate the transition.
            super.setSharedElementEnterTransition( inflateTransition(initUiProcedure) )
        } else {
            // The fragment is being recreated. Just restore the UI.
            initUiProcedure()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val display = super.requireActivity().windowManager.defaultDisplay
        RecipeToolbarUtils.setMaximumHeight(appbarLayout, display)
        RecipeToolbarUtils.setupOffsetListener(appbarLayout, scrltContent)
    }
}