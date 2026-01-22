package com.onthecrow.nomadrates.conversion.view.pair

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.onthecrow.nomadrates.conversion.model.ConversionListItem
import com.onthecrow.nomadrates.conversion.model.ListGroup
import com.onthecrow.nomadrates.ui.SageGreen

internal class ConversionListItemStateProvider : PreviewParameterProvider<ConversionListItem> {
    override val values: Sequence<ConversionListItem> = sequenceOf(
        ConversionListItem.Header("Featured"),
        ConversionListItem.Data(
            fromIcon = "",
            toIcon = "",
            title = "EUR to USD",
            subtitle = "1 EUR = 1.172 USD",
            chartData = listOf(
                917f, 915.381333f, 915.381333f, 915.442733f, 915.381333f,
                917f, 917f, 917f, 915.381333f, 915.381333f,
                915.4288f, 912.2861f, 912.2861f, 912.2861f, 912.2861f,
                912.2861f, 915.4287f, 915.428667f, 912.286f, 912.286f,
                917f, 917f, 914.643f, 915.428667f, 915.02f,
                915.428667f, 915.428667f, 917f, 917f, 915.4287f
            ).map { value -> value.toDouble() },
            chartColor = SageGreen,
            listGroup = ListGroup.FEATURED,
        )
    )
}
