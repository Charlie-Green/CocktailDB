package by.vadim_churun.individual.cocktaildb.ui.drink

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.vm.represent.DrinksList
import kotlinx.android.synthetic.main.drinks_listitem.view.*


class DrinksAdapter(
    val context: Context,
    val list: DrinksList
): RecyclerView.Adapter<DrinksAdapter.DrinkViewHolder>() {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // VIEW HOLDER:

    class DrinkViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgvThumb = itemView.imgvThumb
        val tvName    = itemView.tvName
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // ADAPTER METHODS IMPLEMENTATION:

    override fun getItemCount(): Int
        = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = LayoutInflater.from(context)
            .inflate(R.layout.drinks_listitem, parent, false)
            .let { DrinkViewHolder(it) }

    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        // holder.imgvThumb = TODO()
        holder.tvName.text = list.at(position).name
    }
}