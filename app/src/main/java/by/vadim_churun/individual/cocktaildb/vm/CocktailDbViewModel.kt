package by.vadim_churun.individual.cocktaildb.vm

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.repo.CocktailRepository
import by.vadim_churun.individual.cocktaildb.vm.represent.*
import by.vadim_churun.individual.cocktaildb.vm.state.SyncState
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

    private var lastSyncMillis = 0L

    private val syncStateMLD = MutableLiveData<SyncState>()
    val syncStateLD: LiveData<SyncState>
        get() = syncStateMLD


    private fun sync(minIntervalMillis: Long, notifyAboutResult: Boolean = false) {
        val now = System.currentTimeMillis()
        if(now - lastSyncMillis < minIntervalMillis)
            return

        syncStateMLD.postValue(SyncState.IN_PROGRESS)
        lastSyncMillis = now
        try {
            cocktailRepo.sync()
            syncStateMLD.postValue(
                if(notifyAboutResult) SyncState.SUCCEEDED else SyncState.NO_SYNC
            )
        } catch(exc: Exception) {
            Log.v("Sync", "${exc.javaClass.name}: ${exc.message}")
            syncStateMLD.postValue(
                if(notifyAboutResult) SyncState.FAILED else SyncState.NO_SYNC
            )
        }
    }

    fun forceSync() {
        viewModelScope.launch(Dispatchers.IO) {
            sync(0L, notifyAboutResult = true)
        }
    }

    fun clearSyncState() {
        syncStateMLD.value = SyncState.NO_SYNC
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE, FIELDS, HELP:

    private val app: Application
        get() = super.getApplication()

    private val cocktailRepo = CocktailRepository(this.app)


    init {
        viewModelScope.launch(Dispatchers.IO) {
            while(true) {
                sync(40000L)
                delay(4000L)
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

    private val drinkThumbMLD = MutableLiveData<DrinkThumb>()

    val drinksListLD: LiveData<DrinksList>
        get() = drinksListMLD

    val drinkThumbLD: LiveData<DrinkThumb>
        get() = drinkThumbMLD

    fun requestThumb(id: Int) {
        viewModelScope.launch(Dispatchers.Main) {
            android.util.Log.v("getThumb", "ID = $id. URL = ${drinksListMLD.value?.byID(id)?.thumbURL}")
            val url = drinksListMLD.value?.byID(id)?.thumbURL ?: return@launch
            withContext(Dispatchers.IO) {
                cocktailRepo.getThumb(url)
            }?.also {
                drinkThumbMLD.value = DrinkThumb(id, it)
            }
        }
    }
}