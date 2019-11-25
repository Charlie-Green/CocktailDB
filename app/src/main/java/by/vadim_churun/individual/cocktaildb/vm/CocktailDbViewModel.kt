package by.vadim_churun.individual.cocktaildb.vm

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
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
    private val SYNC_STATE_LOCK = Any()

    private val syncStateMLD = MutableLiveData<SyncState>()
    val syncStateLD: LiveData<SyncState>
        get() = syncStateMLD


    /** @return whether the state was set successfully. **/
    private fun setSyncState(state: SyncState): Boolean {
        synchronized(SYNC_STATE_LOCK) {
            if(state == SyncState.IN_PROGRESS && syncStateMLD.value == SyncState.IN_PROGRESS)
                return false
            syncStateMLD.postValue(state)
            return true
        }
    }

    /** @return whether a sync did occur. **/
    private fun sync(minIntervalMillis: Long, notifyAboutResult: Boolean = false): Boolean {
        val now = System.currentTimeMillis()
        if(now - lastSyncMillis < minIntervalMillis ||
            !setSyncState(SyncState.IN_PROGRESS) )
            return false
        lastSyncMillis = now

        try {
            cocktailRepo.sync()
            setSyncState(
                if(notifyAboutResult) SyncState.SUCCEEDED else SyncState.NO_SYNC
            )
        } catch(exc: Exception) {
            Log.v("Sync", "${exc.javaClass.name}: ${exc.message}")
            setSyncState(
                if(notifyAboutResult) SyncState.FAILED else SyncState.NO_SYNC
            )
        }

        return true
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
                sync(180000L)
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


    ////////////////////////////////////////////////////////////////////////////////////////////
    // INGREDIENTS:

    private val recipeMLD = MutableLiveData<ItemizedRecipe>()

    suspend fun getRecipe(drinkID: Int)
        = withContext(Dispatchers.IO) {
            ItemizedRecipe.fromEntity(cocktailRepo.getRecipe(drinkID))
        }
}