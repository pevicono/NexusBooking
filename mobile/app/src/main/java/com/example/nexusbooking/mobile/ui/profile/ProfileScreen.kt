package com.example.nexusbooking.mobile.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.R
import com.example.nexusbooking.mobile.ui.components.LoadingOverlay
import com.example.nexusbooking.mobile.ui.components.NexusCard
import com.example.nexusbooking.mobile.ui.components.NexusIconButton
import com.example.nexusbooking.mobile.ui.components.NexusPrimaryButton
import com.example.nexusbooking.mobile.ui.components.NexusSecondaryButton
import com.example.nexusbooking.mobile.ui.components.NexusTextField
import com.example.nexusbooking.mobile.ui.components.NexusTopAppBar
import com.example.nexusbooking.mobile.ui.navigation.HomeTab

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToTab: (HomeTab) -> Unit,
    currentTab: HomeTab = HomeTab.DASHBOARD,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var activePane by remember { mutableStateOf(ProfilePane.VIEW) }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) onLogout()
    }

    Scaffold(
        topBar = {
            NexusTopAppBar(
                titleContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nexus_logo_nobg),
                            contentDescription = "Nexus Logo",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                        val titleText = buildAnnotatedString {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onPrimary)) {
                                append("Nexus")
                            }
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                                append("Booking")
                            }
                        }
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    NexusIconButton(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = stringResource(R.string.logout_label),
                        onClick = { viewModel.logout() }
                    )
                },
                centered = false
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, stringResource(R.string.nav_dashboard)) },
                    label = { Text(stringResource(R.string.nav_dashboard)) },
                    selected = false,
                    onClick = { onNavigateToTab(HomeTab.DASHBOARD) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, stringResource(R.string.nav_bookings)) },
                    label = { Text(stringResource(R.string.nav_bookings)) },
                    selected = false,
                    onClick = { onNavigateToTab(HomeTab.BOOKINGS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, stringResource(R.string.nav_groups)) },
                    label = { Text(stringResource(R.string.nav_groups)) },
                    selected = false,
                    onClick = { onNavigateToTab(HomeTab.GROUPS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, stringResource(R.string.nav_facilities)) },
                    label = { Text(stringResource(R.string.nav_facilities)) },
                    selected = false,
                    onClick = { onNavigateToTab(HomeTab.FACILITIES) }
                )
                if (state.user?.role == "ADMIN") {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, stringResource(R.string.nav_admin)) },
                        label = { Text(stringResource(R.string.nav_admin)) },
                        selected = false,
                        onClick = { onNavigateToTab(HomeTab.ADMIN) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.user == null) {
                LoadingOverlay()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.profile_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    AnimatedContent(targetState = activePane, label = "profilePane") { pane ->
                        when (pane) {
                            ProfilePane.VIEW -> ViewPane(state, onEdit = { activePane = ProfilePane.EDIT })
                            ProfilePane.EDIT -> EditPane(
                                state = state,
                                onSave = { newEmail ->
                                    viewModel.updateEmail(newEmail)
                                    activePane = ProfilePane.VIEW
                                },
                                onCancel = { activePane = ProfilePane.VIEW },
                                onChangePassword = { activePane = ProfilePane.CHANGE_PASSWORD }
                            )
                            ProfilePane.CHANGE_PASSWORD -> ChangePasswordPane(
                                state = state,
                                onSave = { current, new ->
                                    viewModel.changePassword(current, new)
                                    activePane = ProfilePane.VIEW
                                },
                                onBack = { activePane = ProfilePane.EDIT }
                            )
                        }
                    }

                    state.error?.let {
                        LaunchedEffect(it) { viewModel.clearMessages() }
                    }
                    state.successMessage?.let {
                        LaunchedEffect(it) { viewModel.clearMessages() }
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewPane(state: ProfileUiState, onEdit: () -> Unit) {
    val user = state.user ?: return
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NexusCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.account_info), style = MaterialTheme.typography.titleMedium)
                ProfileField(label = "Email", value = user.email)
                ProfileField(label = stringResource(R.string.role_label), value = user.role)
            }
        }
        NexusPrimaryButton(
            text = stringResource(R.string.edit_profile_button),
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EditPane(
    state: ProfileUiState,
    onSave: (String) -> Unit,
    onCancel: () -> Unit,
    onChangePassword: () -> Unit
) {
    var email by remember(state.user?.email) { mutableStateOf(state.user?.email ?: "") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.change_email_title), style = MaterialTheme.typography.titleMedium)
                NexusTextField(
                    value = email,
                    onValueChange = { email = it; validationError = null },
                    label = stringResource(R.string.new_email_label),
                    isError = validationError != null,
                    errorMessage = validationError
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NexusSecondaryButton(
                text = stringResource(R.string.cancel_button),
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
            NexusPrimaryButton(
                text = stringResource(R.string.save_button),
                onClick = {
                    if (email.isBlank()) validationError = "El email no puede estar vacío"
                    else onSave(email)
                },
                isLoading = state.isLoading,
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.change_password_button))
        }
    }
}

@Composable
private fun ChangePasswordPane(
    state: ProfileUiState,
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var current by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(stringResource(R.string.change_password_title), style = MaterialTheme.typography.titleMedium)
                NexusTextField(
                    value = current,
                    onValueChange = { current = it; validationError = null },
                    label = stringResource(R.string.current_password_label),
                    isPassword = true
                )
                NexusTextField(
                    value = new,
                    onValueChange = { new = it; validationError = null },
                    label = stringResource(R.string.new_password_label),
                    isPassword = true
                )
                NexusTextField(
                    value = confirm,
                    onValueChange = { confirm = it; validationError = null },
                    label = "Confirmar contraseña",
                    isPassword = true,
                    isError = validationError != null,
                    errorMessage = validationError
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NexusSecondaryButton(
                text = stringResource(R.string.back_button),
                onClick = onBack,
                modifier = Modifier.weight(1f)
            )
            NexusPrimaryButton(
                text = stringResource(R.string.save_button),
                onClick = {
                    when {
                        current.isBlank() || new.isBlank() || confirm.isBlank() ->
                            validationError = "Completa todos los campos"
                        new.length < 6 ->
                            validationError = "Mínimo 6 caracteres"
                        new != confirm ->
                            validationError = "Las contraseñas no coinciden"
                        else -> onSave(current, new)
                    }
                },
                isLoading = state.isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}

private enum class ProfilePane { VIEW, EDIT, CHANGE_PASSWORD }
