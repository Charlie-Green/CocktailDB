package by.vadim_churun.individual.cocktaildb.vm.represent

import android.graphics.Bitmap
import by.vadim_churun.individual.cocktaildb.db.entity.DrinkHeaderEntity


class DrinksList(
    private val list: List<DrinkHeaderEntity>
) {
    private val idMap = HashMap<Int, DrinkHeaderEntity>(list.size)
    private val positionMap = HashMap<Int, Int>(list.size)
    private val imgs = HashMap<Int, Bitmap?>(list.size)
    init {
        for(j in 0 until list.size) {
            val drink = list[j]
            idMap[drink.ID] = drink
            positionMap[drink.ID] = j
            imgs[drink.ID] = null
        }
    }

    val size
        get() = list.size

    fun at(position: Int)
        = list[position]

    fun byID(id: Int)
        = idMap[id]

    fun positionFor(id: Int)
        = positionMap[id]

    fun imageFor(id: Int)
        = imgs[id]

    fun setImageFor(id: Int, image: Bitmap): Boolean {
        if(!idMap.containsKey(id))
            return false
        imgs[id] = image
        return true
    }
}