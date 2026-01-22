package com.onthecrow.nomadrates.conversion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import coil3.compose.AsyncImage
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import nomadrates.feature.conversion.ui_impl.generated.resources.Res
import nomadrates.feature.conversion.ui_impl.generated.resources.ic_drop_down
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun ConversionCurrencyView(
    currencyCode: String,
    currencyIcon: String,
    value: String,
    onCurrencyClick: () -> Unit = {},
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(100.dp),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.fillMaxHeight()
                .padding(8.dp)
                .clip(RoundedCornerShape(100.dp))
                .clickable(enabled = true, onClick = onCurrencyClick),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxHeight()
                    .aspectRatio(1f),
                model = currencyIcon,
                contentDescription = null,
            )
            Text(
                text = currencyCode,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = vectorResource(Res.drawable.ic_drop_down),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
        BasicTextField(
            modifier = Modifier.weight(1f)
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
                        modifier = Modifier.fillMaxHeight()
                            .width(20.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.secondaryContainer, Color.Transparent
                                    )
                                )
                            )
                            .align(Alignment.CenterStart),
                    )
                }
            }
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
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            currencyIcon = "",
            currencyCode = "",
            value = "",
        )
    }
}
