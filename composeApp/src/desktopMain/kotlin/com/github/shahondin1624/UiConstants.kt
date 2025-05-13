package com.github.shahondin1624

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

object UiConstants {
    val PADDING_VERY_SMALL = 2.dp
    val PADDING_SMALL = 4.dp
    val SPACER_HEIGHT_DEFAULT = 8.dp
    val SPACER_HEIGHT_LARGE = 16.dp

    object Table {
        const val TITLE_COLUMN_WEIGHT = 0.2f
        const val TOTAL_COLUMN_WEIGHT = TITLE_COLUMN_WEIGHT
        const val TOTAL_TODAY_COLUMN_WEIGHT = TITLE_COLUMN_WEIGHT
        const val ELAPSED_COLUMN_WEIGHT = TITLE_COLUMN_WEIGHT
        const val RECORD_COLUMN_WEIGHT = TITLE_COLUMN_WEIGHT
    }

    object Text {
        val BIG_TEXT_SIZE = TextUnit(16f, TextUnitType.Sp)
    }
}