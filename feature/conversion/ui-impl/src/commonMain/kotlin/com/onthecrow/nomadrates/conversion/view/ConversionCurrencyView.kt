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
import androidx.compose.ui.graphics.luminance
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

private data class ConversionCurrencyContainerColors(
    val background: Color,
    val content: Color,
)

@Composable
internal fun ConversionCurrencyView(
    state: ConversionCurrencyViewState,
    onCurrencyClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = conversionCurrencyContainerColors()

    ConversionCurrencyContainer(
        modifier = modifier,
        colors = colors,
    ) {
        when (state) {
            ConversionCurrencyViewState.Loading -> ConversionCurrencyLoadingContent(colors)
            is ConversionCurrencyViewState.Content -> ConversionCurrencyContent(
                colors = colors,
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
    colors: ConversionCurrencyContainerColors,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .background(
                color = colors.background,
                shape = ConversionCurrencyContainerShape,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.ConversionCurrencyLoadingContent(
    colors: ConversionCurrencyContainerColors,
) {
    ConversionCurrencyLoadingSelector(colors)
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
private fun ConversionCurrencyLoadingSelector(
    colors: ConversionCurrencyContainerColors,
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
    colors: ConversionCurrencyContainerColors,
    state: ConversionCurrencyViewState.Content,
    onCurrencyClick: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    ConversionCurrencySelector(
        colors = colors,
        state = state,
        onClick = onCurrencyClick,
    )
    ConversionCurrencyValueField(
        colors = colors,
        value = state.conversionValue,
        onValueChange = onValueChange,
    )
}

@Composable
private fun ConversionCurrencySelector(
    colors: ConversionCurrencyContainerColors,
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
        contentColor = colors.content,
    )
}

@Composable
private fun RowScope.ConversionCurrencyValueField(
    colors: ConversionCurrencyContainerColors,
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
            color = colors.content,
        ),
        cursorBrush = SolidColor(colors.content),
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
                                    colors.background,
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

@Composable
private fun conversionCurrencyContainerColors(): ConversionCurrencyContainerColors {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = colorScheme.background.luminance() < 0.5f

    return if (isDarkTheme) {
        ConversionCurrencyContainerColors(
            background = colorScheme.secondaryContainer,
            content = colorScheme.onSecondaryContainer,
        )
    } else {
        ConversionCurrencyContainerColors(
            background = colorScheme.surfaceContainerHighest,
            content = colorScheme.onSecondaryContainer,
        )
    }
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
