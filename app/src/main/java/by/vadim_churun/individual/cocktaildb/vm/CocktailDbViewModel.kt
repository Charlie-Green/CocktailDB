package by.vadim_churun.individual.cocktaildb.vm

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import by.vadim_churun.individual.cocktaildb.repo.CocktailRepository
import by.vadim_churun.individual.cocktaildb.vm.represent.DrinksList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
    // LIFECYCLE, FIELDS, HELP:

    private val app: Application
        get() = super.getApplication()

    private val cocktailRepo = CocktailRepository(this.app)


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