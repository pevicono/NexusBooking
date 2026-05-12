package com.example.nexusbooking.mobile.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.ui.theme.NexusBlueDark
import com.example.nexusbooking.mobile.ui.theme.NexusBluePrimary
import androidx.compose.material3.MaterialTheme

@Composable
fun SplashScreen(
    onLoggedIn: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (!state.isLoading) {
            if (state.isLoggedIn == true) {
                onLoggedIn()
            } else {
                onLoggedOut()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusBlueDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "N",
                    fontSize = 60.sp,
                    color = NexusBluePrimary,
                    style = MaterialTheme.typography.displayMedium
                )
            }

            val titleText = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White, fontSize = 32.sp)) {
                    append("Nexus")
                }
                withStyle(SpanStyle(color = NexusBluePrimary, fontSize = 32.sp)) {
                    append("Booking")
                }
            }

            Text(
                text = titleText,
                style = MaterialTheme.typography.displaySmall
            )

            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )

            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }
    }
}
