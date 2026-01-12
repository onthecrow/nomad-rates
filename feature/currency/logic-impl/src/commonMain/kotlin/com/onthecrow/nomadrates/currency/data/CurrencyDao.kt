package com.onthecrow.nomadrates.currency.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: CurrencyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencies: List<CurrencyEntity>)

    @Query("SELECT * FROM currency WHERE id = :id")
    suspend fun getCurrencyById(id: String): CurrencyEntity?

    @Query("SELECT * FROM currency WHERE id = :id")
    fun getCurrencyByIdFlow(id: String): Flow<CurrencyEntity?>

    @Query("SELECT * FROM currency")
    fun getAllCurrenciesFlow(): Flow<List<CurrencyEntity>>

    @Update
    suspend fun update(currency: CurrencyEntity)

    @Delete
    suspend fun delete(currency: CurrencyEntity)

    @Query("DELETE FROM currency WHERE id = :id")
    suspend fun deleteById(id: String)
}
