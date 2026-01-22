package com.onthecrow.nomadrates.conversion.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Suppress("unused")
@Dao
internal interface ConversionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversion: ConversionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(conversions: List<ConversionEntity>)

    @Query("SELECT * FROM conversion")
    fun getAllConversionsFlow(): Flow<List<ConversionEntity>>

    @Update
    suspend fun update(conversion: ConversionEntity)

    @Delete
    suspend fun delete(conversion: ConversionEntity)

    @Query("DELETE FROM conversion WHERE id = :id")
    suspend fun deleteById(id: String)
}
