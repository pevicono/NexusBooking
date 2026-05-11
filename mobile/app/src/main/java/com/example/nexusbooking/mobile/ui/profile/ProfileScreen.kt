package com.example.nexusbooking.mobile.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.ui.components.LoadingOverlay
import com.example.nexusbooking.mobile.ui.components.NexusCard
import com.example.nexusbooking.mobile.ui.components.NexusIconButton
import com.example.nexusbooking.mobile.ui.components.NexusPrimaryButton
import com.example.nexusbooking.mobile.ui.components.NexusSecondaryButton
import com.example.nexusbooking.mobile.ui.components.NexusTextField
import com.example.nexusbooking.mobile.ui.components.NexusTopAppBar

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
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
                title = "Mi Perfil",
                actions = {
                    NexusIconButton(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        onClick = { viewModel.logout() }
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.user == null) {
                LoadingOverlay()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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
    NexusCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Información de cuenta", style = MaterialTheme.typography.titleMedium)
            ProfileField(label = "Email", value = user.email)
            ProfileField(label = "Rol", value = user.role)
        }
    }
    NexusPrimaryButton(
        text = "Editar Perfil",
        onClick = onEdit
    )
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
                Text("Cambiar Email", style = MaterialTheme.typography.titleMedium)
                NexusTextField(
                    value = email,
                    onValueChange = { email = it; validationError = null },
                    label = "Nuevo email",
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
                text = "Cancelar",
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            )
            NexusPrimaryButton(
                text = "Guardar",
                onClick = {
                    if (email.isBlank()) validationError = "El email no puede estar vacío"
                    else onSave(email)
                },
                isLoading = state.isLoading,
                modifier = Modifier.weight(1f)
            )
        }

        TextButton(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
            Text("Cambiar contraseña")
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
                Text("Cambiar Contraseña", style = MaterialTheme.typography.titleMedium)
                NexusTextField(
                    value = current,
                    onValueChange = { current = it; validationError = null },
                    label = "Contraseña actual",
                    isPassword = true
                )
                NexusTextField(
                    value = new,
                    onValueChange = { new = it; validationError = null },
                    label = "Nueva contraseña",
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
                text = "Atrás",
                onClick = onBack,
                modifier = Modifier.weight(1f)
            )
            NexusPrimaryButton(
                text = "Guardar",
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
