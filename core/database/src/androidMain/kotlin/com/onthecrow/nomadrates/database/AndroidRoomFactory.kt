package com.onthecrow.nomadrates.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.reflect.KClass

internal class AndroidRoomFactory(private val context: Context) : RoomFactory() {

    override fun <T : RoomDatabase> createByClass(
        name: String,
        klass: KClass<T>,
        factory: () -> T,
        config: (RoomDatabase.Builder<T>) -> Unit
    ): T {
        val builder = Room.databaseBuilder(context, klass.java, name)
            .applyCommonConfig()

        config(builder)
        return builder.build()
    }
}
