package com.example.nexusbooking.mobile.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Row

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NexusTopAppBar(
    title: String? = null,
    titleContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    centered: Boolean = true
) {
    val titleComposable = titleContent ?: {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }

    if (centered) {
        CenterAlignedTopAppBar(
            title = titleComposable,
            modifier = modifier,
            navigationIcon = { navigationIcon?.invoke() ?: Unit },
            actions = { actions?.invoke() ?: Unit },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    } else {
        androidx.compose.material3.TopAppBar(
            title = titleComposable,
            modifier = modifier,
            navigationIcon = { navigationIcon?.invoke() ?: Unit },
            actions = { actions?.invoke() ?: Unit },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@Composable
fun NexusIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
