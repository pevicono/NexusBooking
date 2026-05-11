package com.example.nexusbooking.mobile.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.ui.components.NexusPrimaryButton
import com.example.nexusbooking.mobile.ui.components.NexusTextField

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val registerState by viewModel.registerState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(loginState.success) {
        if (loginState.success) onLoginSuccess()
    }
    LaunchedEffect(registerState.success) {
        if (registerState.success) {
            selectedTab = 0
            viewModel.clearRegisterError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "NexusBooking",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(bottom = 48.dp),
            color = MaterialTheme.colorScheme.primary
        )

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; viewModel.clearLoginError() }) {
                Text("Iniciar sesión", modifier = Modifier.padding(vertical = 12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; viewModel.clearRegisterError() }) {
                Text("Registrarse", modifier = Modifier.padding(vertical = 12.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(selectedTab == 0) {
            LoginForm(
                isLoading = loginState.isLoading,
                error = loginState.error,
                onLogin = { email, password -> viewModel.login(email, password) }
            )
        }

        AnimatedVisibility(selectedTab == 1) {
            RegisterForm(
                isLoading = registerState.isLoading,
                error = registerState.error,
                onRegister = { email, password -> viewModel.register(email, password) }
            )
        }
    }
}

@Composable
private fun LoginForm(
    isLoading: Boolean,
    error: String?,
    onLogin: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusTextField(
            value = email,
            onValueChange = { email = it; validationError = null },
            label = "Email",
            isError = validationError != null || error != null,
            errorMessage = validationError ?: error
        )
        NexusTextField(
            value = password,
            onValueChange = { password = it; validationError = null },
            label = "Contraseña",
            isPassword = true,
            isError = false
        )

        NexusPrimaryButton(
            text = "Entrar",
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    validationError = "Completa todos los campos"
                } else {
                    onLogin(email, password)
                }
            },
            isLoading = isLoading
        )
    }
}

@Composable
private fun RegisterForm(
    isLoading: Boolean,
    error: String?,
    onRegister: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusTextField(
            value = email,
            onValueChange = { email = it; validationError = null },
            label = "Email",
            isError = validationError != null || error != null,
            errorMessage = validationError ?: error
        )
        NexusTextField(
            value = password,
            onValueChange = { password = it; validationError = null },
            label = "Contraseña",
            isPassword = true
        )
        NexusTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; validationError = null },
            label = "Confirmar contraseña",
            isPassword = true
        )

        NexusPrimaryButton(
            text = "Crear cuenta",
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                        validationError = "Completa todos los campos"
                    password.length < 6 ->
                        validationError = "Mínimo 6 caracteres"
                    password != confirmPassword ->
                        validationError = "Las contraseñas no coinciden"
                    else -> onRegister(email, password)
                }
            },
            isLoading = isLoading
        )
    }
}
