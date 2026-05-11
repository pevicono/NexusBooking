package com.example.nexusbooking.mobile.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.R
import com.example.nexusbooking.mobile.ui.components.NexusPrimaryButton
import com.example.nexusbooking.mobile.ui.components.NexusTextField
import com.example.nexusbooking.mobile.ui.theme.NexusBlueDark
import com.example.nexusbooking.mobile.ui.theme.NexusBluePrimary

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
        LoginTitleHeader(modifier = Modifier.padding(bottom = 48.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0; viewModel.clearLoginError() }) {
                Text(stringResource(R.string.login_tab), modifier = Modifier.padding(vertical = 12.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1; viewModel.clearRegisterError() }) {
                Text(stringResource(R.string.register_tab), modifier = Modifier.padding(vertical = 12.dp))
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
private fun LoginTitleHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.nexus_logo_nobg),
            contentDescription = "Nexus Logo",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Fit
        )

        val titleText = buildAnnotatedString {
            withStyle(SpanStyle(color = NexusBlueDark, letterSpacing = 1.sp)) {
                append("Nexus")
            }
            withStyle(SpanStyle(color = NexusBluePrimary, letterSpacing = 1.sp)) {
                append("Booking")
            }
        }

        Text(
            text = titleText,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.labelSmall,
            color = NexusBlueDark,
            letterSpacing = 1.sp
        )
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

    val emailLabel = stringResource(R.string.email_label)
    val passwordLabel = stringResource(R.string.password_label)
    val loginButtonText = stringResource(R.string.login_button)
    val completeFieldsError = stringResource(R.string.complete_all_fields_error)

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusTextField(
            value = email,
            onValueChange = { email = it; validationError = null },
            label = emailLabel,
            isError = validationError != null || error != null,
            errorMessage = validationError ?: error
        )
        NexusTextField(
            value = password,
            onValueChange = { password = it; validationError = null },
            label = passwordLabel,
            isPassword = true,
            isError = false
        )

        NexusPrimaryButton(
            text = loginButtonText,
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    validationError = completeFieldsError
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

    val emailLabel = stringResource(R.string.email_label)
    val passwordLabel = stringResource(R.string.password_label)
    val confirmPasswordLabel = stringResource(R.string.confirm_password_label)
    val registerButtonText = stringResource(R.string.register_button)
    val completeFieldsError = stringResource(R.string.complete_all_fields_error)
    val minPasswordLengthError = stringResource(R.string.min_password_length_error)
    val passwordsMismatchError = stringResource(R.string.passwords_dont_match_error)

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        NexusTextField(
            value = email,
            onValueChange = { email = it; validationError = null },
            label = emailLabel,
            isError = validationError != null || error != null,
            errorMessage = validationError ?: error
        )
        NexusTextField(
            value = password,
            onValueChange = { password = it; validationError = null },
            label = passwordLabel,
            isPassword = true
        )
        NexusTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; validationError = null },
            label = confirmPasswordLabel,
            isPassword = true
        )

        NexusPrimaryButton(
            text = registerButtonText,
            onClick = {
                when {
                    email.isBlank() || password.isBlank() || confirmPassword.isBlank() ->
                        validationError = completeFieldsError
                    password.length < 6 ->
                        validationError = minPasswordLengthError
                    password != confirmPassword ->
                        validationError = passwordsMismatchError
                    else -> onRegister(email, password)
                }
            },
            isLoading = isLoading
        )
    }
}
