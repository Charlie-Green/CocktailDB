package by.vadim_churun.individual.cocktaildb.vm.represent

import android.graphics.Bitmap
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkHeaderEntity


class DrinksList(
    private val list: List<DrinkHeaderEntity>
) {
    private val idMap = HashMap<Int, DrinkHeaderEntity>(list.size)
    private val imgs = MutableList<Bitmap?>(list.size) { null }
    init {
        for(drink in list) {
            idMap[drink.ID] = drink
        }
    }

    val size
        get() = list.size

    fun at(position: Int)
        = list[position]

    fun byID(id: Int)
        = idMap[id]

    fun imageAt(position: Int)
        = imgs[position]

    fun setImageAt(position: Int, image: Bitmap) {
        imgs[position] = image
    }
}