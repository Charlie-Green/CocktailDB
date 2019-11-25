package by.vadim_churun.individual.cocktaildb.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import by.vadim_churun.individual.cocktaildb.db.entity.*


@Dao
interface DrinkDAO {
    @Query("select count(*) from Drinks")
    fun count(): Int

    @Query("select id, name, thumb from Drinks order by dateModified desc")
    fun getHeadersLD(): LiveData< List<DrinkHeaderEntity> >

    @Query("select * from Drinks where id=:id")
    fun get(id: Int): DrinkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdate(drinks: List<DrinkEntity>)

    @Delete
    fun delete(drinks: List<DrinkEntity>): Int
}