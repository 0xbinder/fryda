package com.fryda.app.presentation.home.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.fryda.app.presentation.theme.TerminalText

@Composable
fun TerminalTextLine(text: String) {
    Text(
        text = text,
        color = TerminalText,
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
}