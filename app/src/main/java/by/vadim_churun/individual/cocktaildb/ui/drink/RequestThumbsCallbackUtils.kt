package by.vadim_churun.individual.cocktaildb.ui.drink

import android.content.Context
import android.net.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.RecyclerView
import by.vadim_churun.individual.cocktaildb.vm.CocktailDbViewModel


internal object RequestThumbsCallbackUtils {
    private var callback: ConnectivityManager.NetworkCallback? = null

    private fun createNetworkCallback(
        ownerActivity: AppCompatActivity, targetList: RecyclerView
    ): ConnectivityManager.NetworkCallback {
        val vm = CocktailDbViewModel.get(ownerActivity)
        return object: ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                if(vm.isLaunchInitial) {
                    vm.forceSync()
                    return
                }

                if(targetList.isEmpty()) return
                val adapter = targetList.adapter as DrinksAdapter? ?: return

                val positionFirst = targetList.getChildAdapterPosition(targetList.children.first())
                val positionLast = targetList.getChildAdapterPosition(targetList.children.last())
                for(position in positionFirst..positionLast) {
                    vm.requestThumb(adapter.list.at(position).ID)
                }
            }
        }
    }

    fun register(ownerActivity: AppCompatActivity, targetList: RecyclerView) {
        val mCallback = callback
            ?: createNetworkCallback(ownerActivity, targetList).also { callback = it}
        val mRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        val netman = ContextCompat
            .getSystemService(ownerActivity, ConnectivityManager::class.java)!!
        netman.registerNetworkCallback(mRequest, mCallback)
    }

    fun unregister(context: Context) {
        val mCallback = callback ?: return
        val netman = ContextCompat.getSystemService(context, ConnectivityManager::class.java)!!
        netman.unregisterNetworkCallback(mCallback)
        callback = null
    }
}