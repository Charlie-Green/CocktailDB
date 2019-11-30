package by.vadim_churun.individual.cocktaildb.ui.drink

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.ui.CocktailDbAbstractFragment
import by.vadim_churun.individual.cocktaildb.vm.CocktailDbViewModel
import by.vadim_churun.individual.cocktaildb.vm.represent.*
import by.vadim_churun.individual.cocktaildb.vm.state.*
import kotlinx.android.synthetic.main.drinks_fragment.*
import kotlinx.coroutines.*


class DrinksFragment: CocktailDbAbstractFragment(R.layout.drinks_fragment) {
    ////////////////////////////////////////////////////////////////////////////////////////////
    // COMPANION:

    companion object {
        private var listPosition = -1

        private fun trySaveListPosition(list: RecyclerView) {
            val layman = list.layoutManager as GridLayoutManager?
            layman?.findFirstVisibleItemPosition()?.also {
                listPosition = it
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // UI:

    private fun displayDrinks(drinks: DrinksList) {
        DrinksFragment.trySaveListPosition(recvDrinks)

        val newAdapter =
            DrinksAdapter(super.requireContext(), drinks, findNavController() ) { id ->
                super.viewModel.requestThumb(id)
            }

        val layman = recvDrinks.layoutManager as GridLayoutManager?
        recvDrinks.layoutManager = layman ?: run {
            val metrs = DisplayMetrics()
            super.requireActivity().windowManager.defaultDisplay.getMetrics(metrs)
            val cardHeight = super.getResources()
                .getDimensionPixelSize(R.dimen.cocktail_card_height)
            GridLayoutManager(super.requireContext(), metrs.widthPixels / cardHeight)
        }
        recvDrinks.swapAdapter(newAdapter, true)

        if(DrinksFragment.listPosition > 0)
            recvDrinks.layoutManager?.scrollToPosition(DrinksFragment.listPosition)
    }

    private fun applyDrinkThumb(thumb: DrinkThumb) {
        val adapter = recvDrinks.adapter as DrinksAdapter? ?: return
        recvDrinks.post {
            adapter.addThumb(thumb)
        }
    }

    private fun notifySyncFailed() {
        AlertDialog.Builder(super.requireContext())
            .setTitle(R.string.error)
            .setMessage(R.string.sync_failed)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showInitialLoadNotification() {
        lifecycleScope.launch(Dispatchers.Main) {
            FirstLaunchNotifUtils.showLoading(super.requireActivity())
        }
    }

    private fun modifyInitialLoadNotification() {
        if(!FirstLaunchNotifUtils.needNotifyLoadFinished)
            return
        lifecycleScope.launch(Dispatchers.Main) {
            FirstLaunchNotifUtils.modifyLoadFinished(super.requireActivity())
        }
    }

    private fun updateStateDependentUi(vm: CocktailDbViewModel) {
        prBarCentral.isVisible =
            (vm.currentSyncState == SyncState.IN_PROGRESS && vm.isLaunchInitial) ||
            (vm.currentDrinksSearchState == SearchState.IN_PROGRESS)
        tvNothingFound.isVisible = (vm.currentDrinksSearchState == SearchState.ACTIVE) &&
            (recvDrinks.adapter?.itemCount == 0)
        prBarSync.isVisible = (vm.currentSyncState == SyncState.IN_PROGRESS)
        fabSync.isVisible = !prBarSync.isVisible
        tvDataWillArrive.isVisible = (!prBarCentral.isVisible) &&
            (!tvNothingFound.isVisible) && (vm.totalDrinkCount == 0)
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // SEARCH VIEW:

    private fun setSearchViewDimensions(width: Int, height: Int) {
        vSearch.layoutParams = (vSearch.layoutParams as Toolbar.LayoutParams?)?.apply {
            this.width = width
            this.height = height
        } ?: Toolbar.LayoutParams(width, height)
    }

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
            super.viewModel.stopSearchDrinks()
            false    // No need to override the default behaviour.
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // SYNC BUTTON:

    private fun setSyncFabMargins(bottom: Int, right: Int) {
        val params = (fabSync.layoutParams as CoordinatorLayout.LayoutParams?)
            ?: CoordinatorLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params.bottomMargin = bottom
        params.rightMargin = right
        fabSync.layoutParams = params
    }

    private fun layoutSyncFab() {
        val small = super.getResources().getDimensionPixelSize(R.dimen.fab_small_margin)
        val big   = super.getResources().getDimensionPixelSize(R.dimen.fab_big_margin)
        when(super.requireActivity().windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0   -> setSyncFabMargins(bottom = big,   right = small)
            Surface.ROTATION_90  -> setSyncFabMargins(bottom = small, right = big)
            Surface.ROTATION_180 -> setSyncFabMargins(bottom = big,   right = small)
            Surface.ROTATION_270 -> setSyncFabMargins(bottom = small, right = small)
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE:

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = super.viewModel; val owner = super.getViewLifecycleOwner()

        layoutSyncFab()
        updateStateDependentUi(vm)
        if(vm.isLaunchInitial)
            showInitialLoadNotification()

        vm.drinksListLD.observe(owner, Observer { drinks ->
            displayDrinks(drinks)
            updateStateDependentUi(vm)
            if(vm.isLaunchInitial && vm.totalDrinkCount != 0) {
                modifyInitialLoadNotification()
                vm.unsetInitialLaunch()
            }
        })

        vm.drinkThumbLD.observe(owner, Observer { thumb ->
            applyDrinkThumb(thumb)
        })

        fabSync.setOnClickListener {
            super.viewModel.forceSync()
        }

        vm.syncStateLD.observe(owner, Observer { newState ->
            updateStateDependentUi(vm)
            if(newState == SyncState.FAILED) {
                notifySyncFailed()
                vm.clearSyncState()
            }
        })

        setSeachViewListeners()
        vm.drinksSearchStateLD.observe(super.getViewLifecycleOwner(), Observer { _ ->
            updateStateDependentUi(vm)
        })
    }

    override fun onStart() {
        super.onStart()
        RequestThumbsCallbackUtils.register(
            super.requireActivity() as AppCompatActivity, recvDrinks )
    }

    override fun onStop() {
        RequestThumbsCallbackUtils.unregister(super.requireContext())
        DrinksFragment.trySaveListPosition(recvDrinks)
        super.onStop()
    }
}