package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthecrow.nomadrates.conversion.model.ConversionCurrencyViewState
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.ui.view.CurrencySelectorView

private val ConversionCurrencySelectorLoadingWidth = 124.dp
private val ConversionCurrencyContainerShape = RoundedCornerShape(100.dp)

@Composable
internal fun ConversionCurrencyView(
    state: ConversionCurrencyViewState,
    onCurrencyClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ConversionCurrencyContainer(modifier = modifier) {
        when (state) {
            ConversionCurrencyViewState.Loading -> ConversionCurrencyLoadingContent()
            is ConversionCurrencyViewState.Content -> ConversionCurrencyContent(
                state = state,
                onCurrencyClick = onCurrencyClick,
                onValueChange = onValueChange,
            )
        }
    }
}

@Composable
private fun ConversionCurrencyContainer(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = ConversionCurrencyContainerShape,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.ConversionCurrencyLoadingContent() {
    ConversionCurrencyLoadingSelector()
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
private fun ConversionCurrencyLoadingSelector(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
            .width(ConversionCurrencySelectorLoadingWidth)
            .clip(RoundedCornerShape(100.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .shimmer(CircleShape),
        )
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(18.dp)
                .shimmer(RoundedCornerShape(10.dp)),
        )
    }
}

@Composable
private fun RowScope.ConversionCurrencyContent(
    state: ConversionCurrencyViewState.Content,
    onCurrencyClick: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    ConversionCurrencySelector(
        state = state,
        onClick = onCurrencyClick,
    )
    ConversionCurrencyValueField(
        value = state.conversionValue,
        onValueChange = onValueChange,
    )
}

@Composable
private fun ConversionCurrencySelector(
    state: ConversionCurrencyViewState.Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CurrencySelectorView(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(100.dp)),
        currencyCode = state.currencyCode,
        currencyIcon = state.currencyIcon,
        onClick = onClick,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun RowScope.ConversionCurrencyValueField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        modifier = modifier
            .weight(1f)
            .height(48.dp),
        value = value,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        onValueChange = onValueChange,
        visualTransformation = CurrencyAmountInputVisualTransformation(),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSecondaryContainer),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                innerTextField()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(20.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    Color.Transparent,
                                )
                            )
                        )
                        .align(Alignment.CenterStart),
                )
            }
        }
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionCurrencyViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionCurrencyView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            state = ConversionCurrencyViewState.Content(
                currencyIcon = "",
                currencyCode = "EUR",
                conversionRate = .0,
                conversionValue = "",
            ),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF1C1B1F,
)
@Composable
private fun ConversionCurrencyViewLoadingPreview() {
    NomadRatesTheme(darkTheme = true) {
        ConversionCurrencyView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            state = ConversionCurrencyViewState.Loading,
        )
    }
}
