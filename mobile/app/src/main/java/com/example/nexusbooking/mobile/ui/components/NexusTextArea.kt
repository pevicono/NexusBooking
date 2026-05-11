package com.example.nexusbooking.mobile.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NexusTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    minLines: Int = 4,
    maxLines: Int = 8,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label.isNotEmpty()) {{ Text(label) }} else null,
        minLines = minLines,
        maxLines = maxLines,
        modifier = modifier,
        shape = MaterialTheme.shapes.small
    )
}
