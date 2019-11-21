package by.vadim_churun.individual.cocktaildb.db.entity

import java.util.Calendar
import androidx.room.*


@Entity(tableName = "Drinks")
class DrinkEntity (
    @PrimaryKey
    @ColumnInfo(name="id")
    val ID: Int,

    @ColumnInfo(index=true)
    val name: String,

    @ColumnInfo(name="thumb")
    val thumbURL: String?,

    val dateModified: Calendar?,

    val recipe: String?
)