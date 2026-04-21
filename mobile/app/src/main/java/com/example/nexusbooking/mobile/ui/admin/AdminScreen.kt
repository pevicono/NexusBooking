package com.example.nexusbooking.mobile.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var userId by remember { mutableStateOf("") }
    var userActive by remember { mutableStateOf("true") }

    var facilityName by remember { mutableStateOf("") }
    var facilityType by remember { mutableStateOf("") }
    var facilityCapacity by remember { mutableStateOf("") }
    var facilityLocation by remember { mutableStateOf("") }
    var facilityDescription by remember { mutableStateOf("") }

    var incidentFacilityId by remember { mutableStateOf("") }
    var incidentTitle by remember { mutableStateOf("") }
    var incidentDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backoffice") },
                actions = { TextButton(onClick = onBack) { Text("Tornar") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Gestio d'usuaris")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = userId, onValueChange = { userId = it }, label = { Text("User ID") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = userActive, onValueChange = { userActive = it }, label = { Text("Active true/false") }, modifier = Modifier.weight(1f))
            }
            Button(onClick = {
                val id = userId.toLongOrNull() ?: return@Button
                viewModel.setUserActive(id, userActive.equals("true", true))
            }, modifier = Modifier.fillMaxWidth()) { Text("Guardar estat usuari") }

            Text("Crear instal.lacio")
            OutlinedTextField(value = facilityName, onValueChange = { facilityName = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = facilityType, onValueChange = { facilityType = it }, label = { Text("Tipus") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = facilityCapacity, onValueChange = { facilityCapacity = it }, label = { Text("Capacitat") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = facilityLocation, onValueChange = { facilityLocation = it }, label = { Text("Localitzacio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = facilityDescription, onValueChange = { facilityDescription = it }, label = { Text("Descripcio") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                viewModel.createFacility(
                    name = facilityName,
                    type = facilityType,
                    capacity = facilityCapacity.toIntOrNull(),
                    location = facilityLocation,
                    description = facilityDescription
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Crear instal.lacio") }

            Text("Crear incidencia")
            OutlinedTextField(value = incidentFacilityId, onValueChange = { incidentFacilityId = it }, label = { Text("Facility ID (opcional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = incidentTitle, onValueChange = { incidentTitle = it }, label = { Text("Titol") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = incidentDescription, onValueChange = { incidentDescription = it }, label = { Text("Descripcio") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                viewModel.createIncident(
                    facilityId = incidentFacilityId.toLongOrNull(),
                    title = incidentTitle,
                    description = incidentDescription
                )
            }, modifier = Modifier.fillMaxWidth()) { Text("Crear incidencia") }

            Text("Usuaris")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(state.users) { user ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "#${user.id} ${user.email} (${user.role}) active=${user.active}",
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            state.error?.let {
                Text(it)
                LaunchedEffect(it) { viewModel.clearMessages() }
            }
            state.success?.let {
                Text(it)
                LaunchedEffect(it) { viewModel.clearMessages() }
            }
        }
    }
}
