package by.vadim_churun.individual.cocktaildb.vm.launch

import android.content.Context


object CocktailDbAppLoads {
    private const val FILENAME = "loads"
    private const val KEY_LOAD_INITIAL = "initial"
    private var isInitial: Boolean? = null

    fun isLaunchInitial(context: Context): Boolean
        = isInitial ?: context
            .getSharedPreferences(FILENAME, 0)
            .getBoolean(KEY_LOAD_INITIAL, true)
            .also { isInitial = it }

    fun unsetInitialLaunch(context: Context) {
        context.getSharedPreferences(FILENAME, 0)
            .edit()
            .putBoolean(KEY_LOAD_INITIAL, false)
            .apply()
        isInitial = false
    }
}