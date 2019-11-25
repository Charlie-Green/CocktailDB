package by.vadim_churun.individual.cocktaildb.ui.drink

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailDbAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.*
import by.vadim_churun.individual.cocktaildb.vm.state.SyncState
import kotlinx.android.synthetic.main.drinks_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DrinksFragment: CocktailDbAbstractFragment(R.layout.drinks_fragment) {
    //////////////////////////////////////////////////////////////////////////////////////////////
    // UI:

    private fun applyDrinks(drinks: DrinksList) {
        val newAdapter = DrinksAdapter(super.requireContext(), drinks, findNavController()) { id ->
            super.viewModel.requestThumb(id)
        }
        val layman = recvCocktails.layoutManager as GridLayoutManager?
        val curPosition = layman?.findFirstVisibleItemPosition()
        recvCocktails.layoutManager = layman ?: run {
            val metrs = DisplayMetrics()
            super.requireActivity().windowManager.defaultDisplay.getMetrics(metrs)
            val cardHeight = super.getResources()
                .getDimensionPixelSize(R.dimen.cocktail_card_height)
            GridLayoutManager(super.requireContext(), metrs.widthPixels / cardHeight)
        }
        recvCocktails.swapAdapter(newAdapter, true)
        curPosition?.also {
            android.util.Log.v("UI", "Scrolling to position $it")
            recvCocktails.layoutManager?.scrollToPosition(it)
        }
    }

    private fun applyDrinkThumb(thumb: DrinkThumb) {
        val adapter = recvCocktails.adapter as DrinksAdapter?
        adapter?.addThumb(thumb)
    }

    private fun notifySyncFailed() {
        AlertDialog.Builder(super.requireContext())
            .setTitle(R.string.error)
            .setMessage(R.string.sync_failed)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun notifyLoading() {
        lifecycleScope.launch(Dispatchers.Main) {
            InitialLaunchNotification.showLoading(super.requireActivity())
        }
    }

    private fun notifyLoadFinished() {
        if(!InitialLaunchNotification.wasCreated) return
        lifecycleScope.launch(Dispatchers.Main) {
            InitialLaunchNotification.modifyLoadFinished(super.requireActivity())
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE:

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prBarInitial.visibility = View.VISIBLE
        val vm = super.viewModel; val owner = super.getViewLifecycleOwner()

        if(vm.isLaunchInitial) {
            notifyLoading()
            super.viewModel.unsetInitialLoad()
        }

        vm.drinksListLD.observe(owner, Observer { drinks ->
            applyDrinks(drinks)
            prBarInitial.isVisible = (drinks.size == 0)
            if(!prBarInitial.isVisible)
                notifyLoadFinished()
        })

        vm.drinkThumbLD.observe(owner, Observer { thumb ->
            applyDrinkThumb(thumb)
        })

        fabSync.setOnClickListener {
            super.viewModel.forceSync()
        }

        vm.syncStateLD.observe(owner, Observer { state ->
            prBarSync.isVisible = (state == SyncState.IN_PROGRESS)
            fabSync.isVisible = !prBarSync.isVisible
            if(state == SyncState.FAILED) {
                notifySyncFailed()
                vm.clearSyncState()
            }
        })
    }
}