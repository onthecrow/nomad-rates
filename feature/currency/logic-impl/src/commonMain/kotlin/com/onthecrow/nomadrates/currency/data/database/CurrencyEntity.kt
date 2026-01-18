package com.onthecrow.nomadrates.currency.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey val id: String,
    val currencyCode: String,
    val conversionRate: Double,
    val isFavourite: Boolean,
    val isFeatured: Boolean,
    val rates: List<Double>,
)
