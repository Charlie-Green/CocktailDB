package by.vadim_churun.individual.cocktaildb.ui.behaviour

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar


class PopUpOnSnackbarBehaviour(context: Context, attrs: AttributeSet?):
CoordinatorLayout.Behavior<View>(context, attrs) {
    override fun layoutDependsOn
    (parent: CoordinatorLayout, child: View, dependency: View): Boolean
        = dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged
    (parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val deltaY = dependency.height - dependency.translationY
        val params = child.layoutParams as CoordinatorLayout.LayoutParams?
            ?: CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        params.bottomMargin = deltaY.toInt()
        child.layoutParams = params
        return true
    }
}