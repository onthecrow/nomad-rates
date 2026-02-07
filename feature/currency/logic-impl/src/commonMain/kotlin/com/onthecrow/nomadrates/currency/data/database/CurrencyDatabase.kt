package com.onthecrow.nomadrates.currency.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.onthecrow.nomadrates.database.CommonTypeConverters

@Database(entities = [CurrencyEntity::class], version = 1)
@TypeConverters(CommonTypeConverters::class)
@ConstructedBy(CurrencyDatabaseConstructor::class)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}

@Suppress("KotlinNoActualForExpect", "AbstractMemberNotImplemented",
    "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"
)
expect object CurrencyDatabaseConstructor : RoomDatabaseConstructor<CurrencyDatabase>
