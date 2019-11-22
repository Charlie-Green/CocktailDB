package by.vadim_churun.individual.cocktaildb.vm.represent

import androidx.core.text.HtmlCompat
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkEntity
import by.vadim_churun.individual.cocktaildb.db.entity.RecipeEntity
import java.text.SimpleDateFormat
import java.util.*


class ItemizedRecipe private constructor(
    val drink: DrinkEntity,
    val ingredientsList: CharSequence?,
    val dateModified: CharSequence?
) {
    companion object {
        private val DATE_FORMAT = SimpleDateFormat(
            /* TODO: Localize this better. */ "dd-MMM-yyyy", Locale.getDefault() )
        private val LISTITEM_MARK = HtmlCompat.fromHtml("&#8266;", 0)

        fun fromEntity(entity: RecipeEntity): ItemizedRecipe {
            if(entity.ingredients.isEmpty())
                return ItemizedRecipe(entity.drink, null, null)

            val sb = StringBuilder(16 * entity.ingredients.size)
            for(pair in entity.ingredients) {
                if(sb.isNotEmpty())
                    sb.append(System.lineSeparator())
                sb.append("$LISTITEM_MARK ${pair.ingredient.name}: ${pair.lot.measure}")
            }

            val dateModified = entity.drink.dateModified?.time
            return ItemizedRecipe(entity.drink, sb, dateModified?.let { DATE_FORMAT.format(it) })
        }
    }
}