package by.vadim_churun.individual.cocktaildb.db.entity

import androidx.room.*


class DrinkHeaderEntity(
    @ColumnInfo(name="id")
    val ID: Int,

    @ColumnInfo(index = true)
    val name: String,

    @ColumnInfo(name="thumb")
    val thumbURL: String?
)