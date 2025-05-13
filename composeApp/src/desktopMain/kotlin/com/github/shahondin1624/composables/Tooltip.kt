package com.github.shahondin1624.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TextTooltip(text: String?, content: @Composable () -> Unit) {
    if (text != null) {
        Tooltip(content = content) {
            Text(
                text = text,
                modifier = Modifier.padding(10.dp)
            )
        }
    } else {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(content: @Composable () -> Unit, tooltip: (@Composable () -> Unit)?) {
    if (tooltip != null) {
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                ) {
                    tooltip()
                }
            },
            content = content
        )
    } else {
        content()
    }
}