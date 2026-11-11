package com.onthecrow.nomadrates.ui.view

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
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.util.shimmer

private val CurrencyAmountSelectorLoadingWidth = 124.dp
private val CurrencyAmountContainerShape = RoundedCornerShape(100.dp)

private data class CurrencyAmountContainerColors(
    val background: Color,
    val content: Color,
)

/**
 * Rounded field with a currency selector on the left and an amount input on the right.
 */
@Composable
fun CurrencyAmountView(
    state: CurrencyAmountViewState,
    onCurrencyClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val colors = conversionCurrencyContainerColors()

    CurrencyAmountContainer(
        modifier = modifier,
        colors = colors,
    ) {
        when (state) {
            CurrencyAmountViewState.Loading -> CurrencyAmountLoadingContent(colors)
            is CurrencyAmountViewState.Content -> CurrencyAmountContent(
                colors = colors,
                state = state,
                onCurrencyClick = onCurrencyClick,
                onValueChange = onValueChange,
            )
        }
    }
}

@Composable
private fun CurrencyAmountContainer(
    modifier: Modifier = Modifier,
    colors: CurrencyAmountContainerColors,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .background(
                color = colors.background,
                shape = CurrencyAmountContainerShape,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun RowScope.CurrencyAmountLoadingContent(
    colors: CurrencyAmountContainerColors,
) {
    CurrencyAmountLoadingSelector(colors)
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
private fun CurrencyAmountLoadingSelector(
    colors: CurrencyAmountContainerColors,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .padding(8.dp)
            .width(CurrencyAmountSelectorLoadingWidth)
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
private fun RowScope.CurrencyAmountContent(
    colors: CurrencyAmountContainerColors,
    state: CurrencyAmountViewState.Content,
    onCurrencyClick: () -> Unit,
    onValueChange: (String) -> Unit,
) {
    CurrencyAmountSelector(
        colors = colors,
        state = state,
        onClick = onCurrencyClick,
    )
    CurrencyAmountValueField(
        colors = colors,
        value = state.conversionValue,
        onValueChange = onValueChange,
    )
}

@Composable
private fun CurrencyAmountSelector(
    colors: CurrencyAmountContainerColors,
    state: CurrencyAmountViewState.Content,
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
private fun RowScope.CurrencyAmountValueField(
    colors: CurrencyAmountContainerColors,
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
private fun conversionCurrencyContainerColors(): CurrencyAmountContainerColors {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = colorScheme.background.luminance() < 0.5f

    return if (isDarkTheme) {
        CurrencyAmountContainerColors(
            background = colorScheme.secondaryContainer,
            content = colorScheme.onSecondaryContainer,
        )
    } else {
        CurrencyAmountContainerColors(
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
private fun CurrencyAmountViewPreview() {
    NomadRatesTheme(darkTheme = true) {
        CurrencyAmountView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            state = CurrencyAmountViewState.Content(
                currencyIcon = "",
                currencyCode = "EUR",
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
private fun CurrencyAmountViewLoadingPreview() {
    NomadRatesTheme(darkTheme = true) {
        CurrencyAmountView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            state = CurrencyAmountViewState.Loading,
        )
    }
}
