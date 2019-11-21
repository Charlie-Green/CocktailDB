package by.vadim_churun.individual.cocktaildb.ui.drink

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import by.vadim_churun.individual.cocktaildb.R
import by.vadim_churun.individual.cocktaildb.vm.represent.DrinksList
import kotlinx.android.synthetic.main.drinks_listitem.view.*


private fun View.fade(fadeIn: Boolean) {
    val alpha1 = if(fadeIn) 0f else 0.4f
    val alpha2 = if(fadeIn) 0.4f else 0f
    this.alpha = alpha2
    val animer = ObjectAnimator.ofFloat(this, "alpha", alpha1, alpha2)
    animer.duration = if(fadeIn) 400L else 800L
    animer.start()
}


class DrinksAdapter(
    val context: Context,
    val list: DrinksList
): RecyclerView.Adapter<DrinksAdapter.DrinkViewHolder>() {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // USER INTERACTION:

    private class HighlightOrClickListener(private val holder: DrinkViewHolder):
    GestureDetector.SimpleOnGestureListener() {
        override fun onDown(event: MotionEvent): Boolean {
            holder.imgvNameHighlight.fade(true)
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            holder.imgvThumb.performClick()
            return false
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // VIEW HOLDER:

    class DrinkViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgvThumb         = itemView.imgvThumb
        val imgvNameHighlight = itemView.imgvNameHighlight
        val tvName            = itemView.tvName
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // ADAPTER METHODS IMPLEMENTATION:

    override fun getItemCount(): Int
        = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = LayoutInflater.from(context)
            .inflate(R.layout.drinks_listitem, parent, false)
            .let { DrinkViewHolder(it) }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: DrinkViewHolder, position: Int) {
        // holder.imgvThumb = TODO()
        holder.tvName.text = list.at(position).name

        val detector = GestureDetector( context, HighlightOrClickListener(holder) )
        holder.imgvThumb.setOnTouchListener { _, event ->
            if(event.actionMasked != MotionEvent.ACTION_UP &&
                event.actionMasked != MotionEvent.ACTION_CANCEL )
                return@setOnTouchListener detector.onTouchEvent(event)
            holder.imgvNameHighlight.fade(false)
            return@setOnTouchListener true
        }
    }
}