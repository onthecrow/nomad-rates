package com.onthecrow.nomadrates.settings.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nomadrates.feature.settings.ui_impl.generated.resources.Res
import nomadrates.feature.settings.ui_impl.generated.resources.ic_github
import nomadrates.feature.settings.ui_impl.generated.resources.ic_instagram
import nomadrates.feature.settings.ui_impl.generated.resources.ic_telegram
import nomadrates.feature.settings.ui_impl.generated.resources.settings_footer_developed_prefix
import nomadrates.feature.settings.ui_impl.generated.resources.settings_footer_handle
import nomadrates.feature.settings.ui_impl.generated.resources.settings_footer_questions
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun SignatureFooterView(
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val developedPrefix = stringResource(Res.string.settings_footer_developed_prefix)
    val handle = stringResource(Res.string.settings_footer_handle)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                append(developedPrefix)
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                    )
                ) {
                    append(handle)
                }
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Light,
            ),
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { uriHandler.openUri(TELEGRAM_URL) }
                    .padding(8.dp),
                imageVector = vectorResource(Res.drawable.ic_telegram),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { uriHandler.openUri(GITHUB_URL) }
                    .padding(8.dp),
                imageVector = vectorResource(Res.drawable.ic_github),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { uriHandler.openUri(INSTAGRAM_URL) }
                    .padding(8.dp),
                imageVector = vectorResource(Res.drawable.ic_instagram),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = stringResource(Res.string.settings_footer_questions),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                fontWeight = FontWeight.Light,
            ),
        )
    }
}

private const val TELEGRAM_URL = "https://t.me/onthecrow"
private const val GITHUB_URL = "https://github.com/onthecrow"
private const val INSTAGRAM_URL = "https://www.instagram.com/onthecrow"

@Preview
@Composable
private fun SignatureFooterViewPreview() {
    MaterialTheme {
        SignatureFooterView()
    }
}
