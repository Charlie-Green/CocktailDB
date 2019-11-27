package by.vadim_churun.individual.cocktaildb.ui.drink

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailDbAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.represent.*
import by.vadim_churun.individual.cocktaildb.vm.state.*
import kotlinx.android.synthetic.main.drinks_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class DrinksFragment: CocktailDbAbstractFragment(R.layout.drinks_fragment) {
    //////////////////////////////////////////////////////////////////////////////////////////////
    // UI:

    private fun displayDrinks(drinks: DrinksList) {
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
        if(!InitialLaunchNotification.needNotifyLoadFinished)
            return
        lifecycleScope.launch(Dispatchers.Main) {
            InitialLaunchNotification.modifyLoadFinished(super.requireActivity())
        }
    }


    private fun setSearchViewDimensions(width: Int, height: Int) {
        vSearch.layoutParams = (vSearch.layoutParams as Toolbar.LayoutParams?)?.apply {
            this.width = width
            this.height = height
        } ?: Toolbar.LayoutParams(width, height)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // SEARCH VIEW:

    private fun setSeachViewListeners() {
        vSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                this@DrinksFragment.viewModel.searchDrinks(newText)
                return true    // Query was handled.
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return true    // Query has been handled in onQueryTextChanged.
            }
        })

        vSearch.setOnSearchClickListener {
            setSearchViewDimensions(MATCH_PARENT, WRAP_CONTENT)
        }

        vSearch.setOnCloseListener {
            setSearchViewDimensions(WRAP_CONTENT, WRAP_CONTENT)
            false    // No need to override the default behaviour.
        }
    }

    private fun applySearchState(state: SearchState) {
        android.util.Log.v("Search", "State: ${state.name}")
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE:

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prBarInitial.visibility = View.VISIBLE
        val vm = super.viewModel; val owner = super.getViewLifecycleOwner()

        prBarInitial.isVisible = vm.isLaunchInitial
        if(vm.isLaunchInitial)
            notifyLoading()

        vm.drinksListLD.observe(owner, Observer { drinks ->
            displayDrinks(drinks)
            if(vm.isLaunchInitial && vm.totalDrinkCount != 0) {
                prBarInitial.visibility = View.GONE
                notifyLoadFinished()
                vm.unsetInitialLaunch()
            }
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

        setSeachViewListeners()
        vm.drinksSearchStateLD.observe(super.getViewLifecycleOwner(), Observer { state ->
            applySearchState(state)
        })
    }
}