package com.onthecrow.nomadrates.conversion.data

import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class ConversionSelectionSessionStore {
    private var runtimeSelectedConversionPair: SelectedConversionPair? = null
    private val selectedConversionPairUpdates =
        MutableSharedFlow<SelectedConversionPair>(extraBufferCapacity = 1)

    fun observeSelectedConversionPairUpdates(): Flow<SelectedConversionPair> {
        return selectedConversionPairUpdates.asSharedFlow()
    }

    fun getRuntimeSelectedConversionPairOrNull(): SelectedConversionPair? {
        return runtimeSelectedConversionPair
    }

    fun getRuntimeSelectedConversionPair(
        defaultValue: SelectedConversionPair,
    ): SelectedConversionPair {
        // The first selected pair in a process is determined by launch/default-pair logic.
        return (runtimeSelectedConversionPair ?: defaultValue).also { selectedConversionPair ->
            runtimeSelectedConversionPair = selectedConversionPair
        }
    }

    suspend fun updateSelectedConversionPair(selectedConversionPair: SelectedConversionPair) {
        runtimeSelectedConversionPair = selectedConversionPair
        selectedConversionPairUpdates.emit(selectedConversionPair)
    }
}
