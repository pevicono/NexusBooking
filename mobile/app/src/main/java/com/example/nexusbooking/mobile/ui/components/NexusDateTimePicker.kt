package com.example.nexusbooking.mobile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusDateTimePicker(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val localDateTime = try {
        LocalDateTime.parse(value, dateFormatter)
    } catch (e: Exception) {
        LocalDateTime.now().withSecond(0).withNano(0)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = if (value.isNotEmpty()) localDateTime.format(displayFormatter) else "Selecciona fecha y hora",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            shape = MaterialTheme.shapes.small,
            enabled = false
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = Instant.ofEpochMilli(millis)
                            val selectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
                            val newDateTime = localDateTime.withYear(selectedDate.year)
                                .withMonth(selectedDate.monthValue)
                                .withDayOfMonth(selectedDate.dayOfMonth)
                            onValueChange(newDateTime.format(dateFormatter))
                        }
                        showDatePicker = false
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
