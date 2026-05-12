package com.example.nexusbooking.mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nexusbooking.mobile.ui.theme.NexusBlueContainer
import com.example.nexusbooking.mobile.ui.theme.NexusError
import com.example.nexusbooking.mobile.ui.theme.NexusSuccess
import com.example.nexusbooking.mobile.ui.theme.NexusWarning
import com.example.nexusbooking.mobile.R

@Composable
fun NexusCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun NexusStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val statusCode = status.uppercase()

    val backgroundColor = when (statusCode) {
        "AVAILABLE" -> NexusSuccess
        "BOOKED" -> NexusError
        "MAINTENANCE" -> NexusWarning
        "ACTIVE" -> NexusSuccess
        "INACTIVE" -> Color(0xFF757575)
        "CONFIRMED", "IN_PROGRESS" -> NexusBlueContainer
        "CANCELLED", "CLOSED", "RESOLVED" -> NexusSuccess
        "OPEN", "PENDING" -> NexusWarning
        else -> MaterialTheme.colorScheme.secondary
    }

    val displayText = when (statusCode) {
        "AVAILABLE" -> stringResource(R.string.status_available)
        "BOOKED" -> stringResource(R.string.status_booked)
        "MAINTENANCE" -> stringResource(R.string.status_maintenance)
        "ACTIVE" -> stringResource(R.string.status_active)
        "INACTIVE" -> stringResource(R.string.status_inactive)
        else -> status
    }

    val textColor = Color.White

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = backgroundColor
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp
            )
        )
    }
}
