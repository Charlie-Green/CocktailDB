package by.vadim_churun.individual.cocktaildb.vm

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import by.vadim_churun.individual.cocktaildb.repo.CocktailRepository
import by.vadim_churun.individual.cocktaildb.vm.represent.DrinksList
import kotlinx.coroutines.*


class CocktailDbViewModel(app: Application): AndroidViewModel(app) {
    ////////////////////////////////////////////////////////////////////////////////////////////
    // COMPANION:

    companion object {
        fun get(owner: ViewModelStoreOwner, app: Application)
            = ViewModelProvider(owner, ViewModelProvider.AndroidViewModelFactory(app))
                .get(CocktailDbViewModel::class.java)

        fun get(activity: AppCompatActivity)
            = get(activity, activity.application)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // SYNC:

    private fun sync()
        = try {
            val count = cocktailRepo.sync()
            Log.v("Sync", "Drinks fetched: $count")
        } catch(exc: Exception) {
            Log.v("Sync", "${exc.javaClass.name}: ${exc.message}")
        }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE, FIELDS, HELP:

    private val app: Application
        get() = super.getApplication()

    private val cocktailRepo = CocktailRepository(this.app)


    init {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                sync()
                delay(/*20000*/ 4000)
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // DRINKS:

    private val drinksListMLD = MediatorLiveData<DrinksList>().apply {
        addSource(cocktailRepo.drinkHeadersLD) { headers ->
            viewModelScope.launch(Dispatchers.Main) {
                value = withContext(Dispatchers.Default) { DrinksList(headers) }
            }
        }
        // TODO: Add source for images.
    }

    val drinksListLD: LiveData<DrinksList>
        get() = drinksListMLD
}