package com.onthecrow.nomadrates.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlin.reflect.KClass

abstract class RoomFactory {
    @PublishedApi
    internal abstract fun <T : RoomDatabase> createByClass(
        name: String,
        klass: KClass<T>,
        factory: () -> T,
        config: (RoomDatabase.Builder<T>) -> Unit
    ): T
}

inline fun <reified T : RoomDatabase> RoomFactory.create(
    name: String,
    noinline factory: () -> T,
    noinline config: (RoomDatabase.Builder<T>) -> Unit = {}
): T {
    return createByClass(name, T::class, factory, config)
}

internal fun <T : RoomDatabase> RoomDatabase.Builder<T>.applyCommonConfig(): RoomDatabase.Builder<T> {
    return this
        .setDriver(BundledSQLiteDriver())
        // todo provide dispatcher via di
        .setQueryCoroutineContext(Dispatchers.IO)
}
