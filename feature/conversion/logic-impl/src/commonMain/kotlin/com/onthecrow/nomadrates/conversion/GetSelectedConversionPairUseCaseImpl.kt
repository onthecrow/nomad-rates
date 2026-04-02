package com.onthecrow.nomadrates.conversion

import com.onthecrow.nomadrates.conversion.data.ConversionSelectionRepository
import com.onthecrow.nomadrates.conversion.domain.GetSelectedConversionPairUseCase
import com.onthecrow.nomadrates.conversion.domain.model.SelectedConversionPair
import com.onthecrow.nomadrates.settings.domain.LaunchPairMode
import com.onthecrow.nomadrates.settings.domain.ObserveDefaultPairUseCase
import com.onthecrow.nomadrates.settings.domain.ObserveLaunchPairModeUseCase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class GetSelectedConversionPairUseCaseImpl(
    private val conversionSelectionRepository: ConversionSelectionRepository,
    private val observeDefaultPairUseCase: ObserveDefaultPairUseCase,
    private val observeLaunchPairModeUseCase: ObserveLaunchPairModeUseCase,
) : GetSelectedConversionPairUseCase {
    override fun invoke(): Flow<SelectedConversionPair> = channelFlow {
        val launchPairMode = observeLaunchPairModeUseCase().first()
        var previousDefaultPair = observeDefaultPairUseCase().first()
        val launchSelectedConversionPair = launchPairMode.toSelectedConversionPair(
            savedSelectedConversionPair = conversionSelectionRepository.getSavedSelectedConversionPair(),
            defaultPair = previousDefaultPair,
        )
        send(
            conversionSelectionRepository.getRuntimeSelectedConversionPair(launchSelectedConversionPair)
        )

        val defaultPairUpdatesJob = launch {
            observeDefaultPairUseCase()
                .drop(1)
                .collect { newDefaultPair ->
                    val currentSelectedConversionPair =
                        conversionSelectionRepository.getCurrentSelectedConversionPairOrNull()
                    if (currentSelectedConversionPair == previousDefaultPair) {
                        conversionSelectionRepository.saveSelectedConversionPair(newDefaultPair)
                    }
                    previousDefaultPair = newDefaultPair
                }
        }

        val selectedPairUpdatesJob = launch {
            conversionSelectionRepository.observeSelectedConversionPairUpdates()
                .collect(::send)
        }

        awaitClose {
            defaultPairUpdatesJob.cancel()
            selectedPairUpdatesJob.cancel()
        }
    }.distinctUntilChanged()
}

private fun LaunchPairMode.toSelectedConversionPair(
    savedSelectedConversionPair: SelectedConversionPair?,
    defaultPair: SelectedConversionPair,
): SelectedConversionPair {
    return when (this) {
        LaunchPairMode.REMEMBER_LAST_PAIR ->
            savedSelectedConversionPair ?: defaultPair

        LaunchPairMode.USE_DEFAULT_PAIR -> defaultPair
    }
}
