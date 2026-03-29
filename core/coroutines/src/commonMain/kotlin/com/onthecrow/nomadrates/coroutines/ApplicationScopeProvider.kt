package com.onthecrow.nomadrates.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

interface ApplicationScopeProvider {
    val applicationScope: CoroutineScope
}

internal class DefaultApplicationScopeProvider(
    dispatchersProvider: DispatchersProvider,
) : ApplicationScopeProvider {
    override val applicationScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatchersProvider.default)
}
