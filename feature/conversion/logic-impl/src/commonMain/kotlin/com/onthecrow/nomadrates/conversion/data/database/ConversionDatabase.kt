package com.onthecrow.nomadrates.conversion.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.onthecrow.nomadrates.database.CommonTypeConverters

@Database(entities = [ConversionEntity::class], version = 1)
@TypeConverters(CommonTypeConverters::class)
@ConstructedBy(ConversionDatabaseConstructor::class)
internal abstract class ConversionDatabase : RoomDatabase() {
    abstract fun conversionDao(): ConversionDao
}

@Suppress("KotlinNoActualForExpect", "AbstractMemberNotImplemented")
internal expect object ConversionDatabaseConstructor : RoomDatabaseConstructor<ConversionDatabase>
