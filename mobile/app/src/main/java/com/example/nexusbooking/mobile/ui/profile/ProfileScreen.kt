package com.example.nexusbooking.mobile.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = { Text("Mi perfil") },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.user == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (activePane) {
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

                    state.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        LaunchedEffect(it) { viewModel.clearMessages() }
                    }
                    state.successMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
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
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileField(label = "Email", value = user.email)
            // Debug only
            // ProfileField(label = "Rol", value = user.role)
            // ProfileField(label = "ID", value = user.id.toString())
        }
    }
    Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Editar perfil")
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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; validationError = null },
            label = { Text("Nuevo email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        validationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
            Button(
                onClick = {
                    if (email.isBlank()) validationError = "El email no puede estar vacío"
                    else onSave(email)
                },
                enabled = !state.isLoading,
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = current,
            onValueChange = { current = it; validationError = null },
            label = { Text("Contraseña actual") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = new,
            onValueChange = { new = it; validationError = null },
            label = { Text("Nueva contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = confirm,
            onValueChange = { confirm = it; validationError = null },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        validationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Atrás") }
            Button(
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
                enabled = !state.isLoading,
                modifier = Modifier.weight(1f)
            ) { Text("Guardar") }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

private enum class ProfilePane { VIEW, EDIT, CHANGE_PASSWORD }
