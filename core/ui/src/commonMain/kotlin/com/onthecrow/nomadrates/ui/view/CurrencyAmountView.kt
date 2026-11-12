package com.onthecrow.nomadrates.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onthecrow.nomadrates.ui.NomadRatesTheme
import com.onthecrow.nomadrates.ui.model.CurrencyAmountViewState
import com.onthecrow.nomadrates.ui.util.shimmer
import com.onthecrow.nomadrates.util.editAmountField
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import kotlinx.coroutines.launch

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
    val fieldValueState = remember { mutableStateOf(value.toFieldValueWithCursorAtEnd()) }
    // The amount can change from outside (a conversion, the other screen); the cursor goes to the end.
    remember(value) {
        if (fieldValueState.value.text != value) {
            fieldValueState.value = value.toFieldValueWithCursorAtEnd()
        }
    }
    val fieldValue = fieldValueState.value
    var isFocused by remember { mutableStateOf(false) }
    val visualTransformation = remember { CurrencyAmountInputVisualTransformation() }
    var textAreaWidth by remember { mutableIntStateOf(0) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val fullSizeStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = AmountFontSize,
        textAlign = TextAlign.End,
        color = colors.content,
    )
    // A long amount gets a smaller font, so it is always seen whole and never scrolled.
    val textStyle = remember(fieldValue.text, textAreaWidth, fullSizeStyle, textMeasurer, density) {
        val displayedText = formatAmountForDisplay(fieldValue.text)
        val availableWidth = textAreaWidth - with(density) { (FadeWidth + CursorAllowance).toPx() }
        val fullWidth = textMeasurer.measure(displayedText, fullSizeStyle, maxLines = 1, softWrap = false).size.width
        if (availableWidth <= 0 || fullWidth <= availableWidth) {
            fullSizeStyle
        } else {
            val fontSize = (AmountFontSize.value * availableWidth / fullWidth).coerceAtLeast(MinAmountFontSize.value)
            fullSizeStyle.copy(fontSize = fontSize.sp)
        }
    }
    val scope = rememberCoroutineScope()
    val exactTaps = remember { ExactTapCursor() }

    /**
     * On iOS a tap in a focused field moves the cursor to the closest word boundary, like the
     * native fields do, and an amount such as "0.004000545" is one word, so the cursor could only
     * go to its start or end. After the field handles a tap, the cursor is put exactly where the
     * user tapped instead.
     */
    fun placeCursorAtTap(tapPosition: Offset) {
        val current = fieldValueState.value
        if (!isFocused || !current.selection.collapsed) return
        val offset = exactTaps.offsetAt(tapPosition) ?: return
        val originalOffset = visualTransformation.filter(AnnotatedString(current.text))
            .offsetMapping
            .transformedToOriginal(offset)
        if (current.selection.start != originalOffset) {
            fieldValueState.value = current.copy(selection = TextRange(originalOffset))
        }
    }

    BasicTextField(
        modifier = modifier
            .weight(1f)
            .height(48.dp)
            .onFocusChanged { focusState -> isFocused = focusState.isFocused },
        value = fieldValue,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        onValueChange = { editedValue ->
            // A tap that only moved the cursor: it goes exactly under the finger.
            // The state, not a value captured by an earlier composition: edits can come faster.
            val current = fieldValueState.value
            exactTaps.takeRecentTap()?.let { tapPosition ->
                if (editedValue.text == current.text && editedValue.selection.collapsed) {
                    fieldValueState.value = editedValue
                    placeCursorAtTap(tapPosition)
                    return@BasicTextField
                }
            }
            // The input rules may add or drop characters; the selection moves with them, so the
            // cursor stays next to the same digits.
            val edit = editAmountField(
                previous = current.text,
                edited = editedValue.text,
                selectionStart = editedValue.selection.start,
                selectionEnd = editedValue.selection.end,
            )
            fieldValueState.value = editedValue.copy(
                text = edit.text,
                selection = TextRange(edit.selectionStart, edit.selectionEnd),
                composition = editedValue.composition.takeIf { edit.text == editedValue.text },
            )
            if (edit.text != current.text) {
                onValueChange(edit.text)
            }
        },
        visualTransformation = visualTransformation,
        singleLine = true,
        textStyle = textStyle,
        onTextLayout = { layoutResult -> exactTaps.textLayout = layoutResult },
        cursorBrush = SolidColor(colors.content),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(end = 24.dp)
                    .fillMaxSize()
                    .clipToBounds()
                    .onSizeChanged { size -> textAreaWidth = size.width }
                    .onPlaced { coordinates -> exactTaps.fieldCoordinates = coordinates }
                    .pointerInput(exactTaps) {
                        detectExactTaps { tapPosition ->
                            exactTaps.recordTap(tapPosition)
                            // The field handles the tap after this; correct the cursor then, also
                            // when the field did not move it.
                            scope.launch {
                                withFrameNanos { }
                                exactTaps.takeRecentTap()?.let(::placeCursorAtTap)
                            }
                        }
                    },
                contentAlignment = Alignment.CenterEnd,
            ) {
                // While the field is not being edited, the amount changes because of the other
                // field or screen, so it is shown with rolling digits instead of the plain text.
                Box(
                    modifier = Modifier
                        .alpha(if (isFocused) 1f else 0f)
                        .onPlaced { coordinates -> exactTaps.textCoordinates = coordinates },
                ) {
                    innerTextField()
                }
                if (!isFocused) {
                    RollingText(
                        modifier = Modifier.wrapContentWidth(align = Alignment.End, unbounded = true),
                        text = formatAmountForDisplay(fieldValue.text),
                        style = textStyle,
                        color = colors.content,
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(FadeWidth)
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

private val AmountFontSize = 24.sp
private val MinAmountFontSize = 12.sp

/** The fade at the start of the amount, where a text that does not fit would be cut. */
private val FadeWidth = 20.dp
private val CursorAllowance = 4.dp

/**
 * Remembers the latest tap on the amount field and finds the text position under it.
 */
private class ExactTapCursor {
    var textLayout: TextLayoutResult? = null
    var fieldCoordinates: LayoutCoordinates? = null
    var textCoordinates: LayoutCoordinates? = null

    private var tapPosition: Offset? = null
    private var tapTime: TimeMark? = null

    fun recordTap(position: Offset) {
        tapPosition = position
        tapTime = TimeSource.Monotonic.markNow()
    }

    /** The tap the field may be handling right now, once. */
    fun takeRecentTap(): Offset? {
        val position = tapPosition ?: return null
        val isRecent = tapTime?.elapsedNow()?.let { it < RECENT_TAP } == true
        tapPosition = null
        tapTime = null
        return position.takeIf { isRecent }
    }

    /**
     * Position in the field text (with thousand groups) under [position], given in the field's
     * coordinates; null when it cannot be known, e.g. when the text is scrolled.
     */
    fun offsetAt(position: Offset): Int? {
        val layout = textLayout ?: return null
        val field = fieldCoordinates?.takeIf { it.isAttached } ?: return null
        val text = textCoordinates?.takeIf { it.isAttached } ?: return null
        // A text wider than the field is scrolled, and the scroll is not known here.
        if (layout.size.width > text.size.width + 1) return null
        val inText = text.localPositionOf(field, position)
        return layout.getOffsetForPosition(
            Offset(
                x = inText.x.coerceIn(0f, layout.size.width.toFloat()),
                y = layout.size.height / 2f,
            )
        )
    }

    private companion object {
        val RECENT_TAP = 500.milliseconds
    }
}

/**
 * Calls [onTap] with the position of every short tap, without consuming anything, so the text
 * field still gets all events.
 */
private suspend fun PointerInputScope.detectExactTaps(onTap: (Offset) -> Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
        val downTime = TimeSource.Monotonic.markNow()
        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial) ?: return@awaitEachGesture
        val isShort = downTime.elapsedNow() < viewConfiguration.longPressTimeoutMillis.milliseconds
        val isStill = (up.position - down.position).getDistance() < viewConfiguration.touchSlop
        if (isShort && isStill) onTap(up.position)
    }
}

private fun String.toFieldValueWithCursorAtEnd(): TextFieldValue =
    TextFieldValue(text = this, selection = TextRange(length))

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
