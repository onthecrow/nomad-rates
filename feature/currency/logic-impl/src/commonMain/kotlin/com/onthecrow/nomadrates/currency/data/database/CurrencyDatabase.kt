package com.onthecrow.nomadrates.currency.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [CurrencyEntity::class], version = 1)
@ConstructedBy(CurrencyDatabaseConstructor::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}

@Suppress("KotlinNoActualForExpect", "AbstractMemberNotImplemented")
expect object CurrencyDatabaseConstructor : RoomDatabaseConstructor<CurrencyDatabase>
