package by.vadim_churun.individual.cocktaildb.ui.search

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.DialogFragment
import by.vadim_churun.individual.cocktaildb.R
import kotlinx.android.synthetic.main.search_dialog.*


abstract class AbstractSearchDialog: DialogFragment() {
    ////////////////////////////////////////////////////////////////////////////////////////////
    // ABSTRACT:

    protected abstract val message: String
    protected abstract val searchHint: String

    protected abstract fun onQuerySubmitted(query: CharSequence)


    ////////////////////////////////////////////////////////////////////////////////////////////
    // COMPANION:

    companion object {
        /** The width the dialog should be. **/
        const val ARG_WIDTH = "width"

        fun getRecommendedWidth(withinDisplay: Display): Int {
            val metrs = DisplayMetrics()
            withinDisplay.getMetrics(metrs)
            return (0.95*metrs.widthPixels).toInt()
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////
    // LIFECYCLE:

    override fun onCreateView
    (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.search_dialog, container, false)
        if(super.getArguments()?.containsKey(ARG_WIDTH) == true)
            v.minimumWidth = super.getArguments()!!.getInt(ARG_WIDTH)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvMessage.text = this.message
        etQuery.hint = this.searchHint

        buSubmit.setOnClickListener {
            val query = etQuery.text
            if(query.isNotEmpty()) {
                super.dismiss()
                onQuerySubmitted(query)
            }
        }
    }
}