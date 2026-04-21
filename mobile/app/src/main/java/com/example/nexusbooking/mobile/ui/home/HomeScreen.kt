package com.example.nexusbooking.mobile.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onOpenAdmin: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NexusBooking") },
                actions = {
                    TextButton(onClick = onOpenProfile) { Text("Perfil") }
                    if (state.user?.role == "ADMIN") {
                        TextButton(onClick = onOpenAdmin) { Text("Backoffice") }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Hola, ${state.user?.email ?: ""}")
            Spacer(Modifier.height(8.dp))

            if (state.user?.role == "ADMIN") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Aquesta pantalla és per usuaris finals.")
                        Text("Amb rol ADMIN, fes servir Backoffice per gestionar el sistema.")
                        Button(onClick = onOpenAdmin, modifier = Modifier.fillMaxWidth()) {
                            Text("Anar a Backoffice")
                        }
                    }
                }
                return@Column
            }

            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Instal.lacions") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Reserves") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Grups") })
            }

            Spacer(Modifier.height(8.dp))
            when (tab) {
                0 -> FacilitiesTab(state)
                1 -> BookingsTab(state, viewModel)
                else -> GroupsTab(state, viewModel)
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

@Composable
private fun FacilitiesTab(state: HomeUiState) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.facilities) { facility ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(facility.name)
                    Text("Tipus: ${facility.type} | Estat: ${facility.status}")
                    Text("Capacitat: ${facility.capacity ?: 0} | Lloc: ${facility.location ?: "-"}")
                }
            }
        }
    }
}

@Composable
private fun BookingsTab(state: HomeUiState, viewModel: HomeViewModel) {
    var facilityId by remember { mutableStateOf("") }
    var groupId by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(defaultStartTime()) }
    var endTime by remember { mutableStateOf(defaultEndTime()) }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(state.facilities, state.groups) {
        if (facilityId.isBlank() && state.facilities.isNotEmpty()) {
            facilityId = state.facilities.first().id.toString()
        }
        if (groupId.isBlank() && state.groups.isNotEmpty()) {
            groupId = state.groups.first().id.toString()
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (state.facilities.isNotEmpty()) {
            Text("Instal·lacions disponibles: ${state.facilities.joinToString { "#${it.id} ${it.name}" }}")
        }
        if (state.groups.isNotEmpty()) {
            Text("Grups disponibles: ${state.groups.joinToString { "#${it.id} ${it.name}" }}")
        }

        OutlinedTextField(value = facilityId, onValueChange = { facilityId = it }, label = { Text("Facility ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = groupId, onValueChange = { groupId = it }, label = { Text("Group ID") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = startTime, onValueChange = { startTime = it }, label = { Text("Inici (2026-05-01T10:00:00)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = endTime, onValueChange = { endTime = it }, label = { Text("Fi (2026-05-01T11:00:00)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            val selectedFacilityId = facilityId.toLongOrNull() ?: return@Button
            val selectedGroupId = groupId.toLongOrNull() ?: return@Button
            viewModel.createBooking(selectedFacilityId, selectedGroupId, startTime, endTime, notes)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Crear reserva")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.bookings) { booking ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("#${booking.id} - ${booking.facilityName}")
                        Text("${booking.startTime} -> ${booking.endTime}")
                        Text("Estat: ${booking.status}")
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupsTab(state: HomeUiState, viewModel: HomeViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom grup") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripcio") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.createGroup(name, description) }, modifier = Modifier.fillMaxWidth()) {
            Text("Crear grup")
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = joinCode, onValueChange = { joinCode = it }, label = { Text("Codi del grup") }, modifier = Modifier.weight(1f))
            Button(onClick = {
                if (joinCode.isBlank()) return@Button
                viewModel.joinGroupByCode(joinCode)
            }) { Text("Unir-me") }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.groups) { group ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(group.name)
                        Text("Membres: ${group.memberCount} | Owner: ${group.ownerEmail}")
                        if (group.members.isNotEmpty()) {
                            HorizontalDivider()
                            Text("Integrants:")
                            group.members.take(4).forEach { member ->
                                Text("• ${member.email} (${member.role})")
                            }
                        }
                        if (state.user?.id != group.ownerId) {
                            Button(onClick = { viewModel.leaveGroup(group.id) }, modifier = Modifier.fillMaxWidth()) {
                                Text("Sortir del grup")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun defaultStartTime(): String {
    val now = LocalDateTime.now().withSecond(0).withNano(0)
    val minute = now.minute
    val delta = (30 - (minute % 30)) % 30
    val adjustedDelta = if (delta == 0) 30 else delta
    return now.plusMinutes(adjustedDelta.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
}

private fun defaultEndTime(): String {
    val start = LocalDateTime.parse(defaultStartTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
    return start.plusHours(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
}
