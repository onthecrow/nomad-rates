package com.onthecrow.nomadrates.conversion.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion")
internal data class ConversionEntity(
    val fromCurrencyCode: String,
    val toCurrencyCode: String,
    val isFeatured: Boolean,
    val isFavorite: Boolean,
    @PrimaryKey val id: String = fromCurrencyCode + toCurrencyCode,
)
