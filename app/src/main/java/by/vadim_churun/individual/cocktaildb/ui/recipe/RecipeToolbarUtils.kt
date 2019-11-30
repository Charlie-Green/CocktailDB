package by.vadim_churun.individual.cocktaildb.ui.recipe

import android.util.DisplayMetrics
import android.view.Display
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.coordinatorlayout.widget.CoordinatorLayout
import by.vadim_churun.individual.cocktaildb.R
import com.google.android.material.appbar.AppBarLayout


/** Used to set up size and behaviour of [RecipeFragment]'s collapsing toolbar. **/
object RecipeToolbarUtils {
    private val ViewGroup.coordinatorParams: CoordinatorLayout.LayoutParams
        get() = this.layoutParams as CoordinatorLayout.LayoutParams?
            ?: CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)


    fun setMaximumHeight(appbar: AppBarLayout, display: Display) {
        val displayHeight = DisplayMetrics().also {
            display.getMetrics(it)
        }.heightPixels
        val params = appbar.coordinatorParams.apply {
            height = displayHeight / 2
        }
    }

    fun setupOffsetListener(appbar: AppBarLayout, content: ViewGroup) {
        fun setContentMargin(margin: Int) {
            content.layoutParams = content.coordinatorParams.apply {
                topMargin = margin
            }
        }

        val res = appbar.context.resources
        val baseHeight = res.getDimensionPixelSize(R.dimen.min_recipe_toolbar_height)
        appbar.addOnOffsetChangedListener(object: AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(abLayout: AppBarLayout, offset: Int) {
                setContentMargin(baseHeight + abLayout.totalScrollRange + offset)
            }
        })
    }
}