package com.onthecrow.nomadrates.database

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSHomeDirectory
import kotlin.reflect.KClass

internal class IosRoomFactory : RoomFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : RoomDatabase> createByClass(
        name: String,
        klass: KClass<T>,
        factory: () -> T,
        config: (RoomDatabase.Builder<T>) -> Unit
    ): T {
        val dbFilePath = NSHomeDirectory() + "/Documents/$name"

        val builder = Room.databaseBuilder<RoomDatabase>(
            name = dbFilePath,
            factory = { factory() as RoomDatabase }
        ) as RoomDatabase.Builder<T>

        builder.applyCommonConfig()
        config(builder)
        return builder.build()
    }
}
