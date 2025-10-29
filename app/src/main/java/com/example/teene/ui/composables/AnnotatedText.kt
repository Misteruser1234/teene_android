package com.example.teene.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink

/**
 * Created by 3100lari on 2025/01/14
 */
@Composable
fun ColoredAndClickableText(
    modifier: Modifier = Modifier,
    firstString: String,
    secondString: String,
    onClick: () -> Unit
)
{
    val annotatedString = buildAnnotatedString {
        append(firstString)
        append(" ")
        withLink(
            link = LinkAnnotation.Clickable(
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                    )
                ),
                tag = "TAG",
                linkInteractionListener = {
                    onClick()
                },
            ),
        ) {
            append(secondString)
        }
    }

    Text(
        modifier = modifier,
        text = annotatedString,
        textAlign = TextAlign.Center,
    )
}


