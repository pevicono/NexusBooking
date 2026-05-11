package com.example.nexusbooking.mobile.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.ui.components.NexusCard
import com.example.nexusbooking.mobile.ui.components.NexusDatePicker
import com.example.nexusbooking.mobile.ui.components.NexusDropdown
import com.example.nexusbooking.mobile.ui.components.NexusIconButton
import com.example.nexusbooking.mobile.ui.components.NexusPrimaryButton
import com.example.nexusbooking.mobile.ui.components.NexusSecondaryButton
import com.example.nexusbooking.mobile.ui.components.NexusStatusBadge
import com.example.nexusbooking.mobile.ui.components.NexusTextArea
import com.example.nexusbooking.mobile.ui.components.NexusTextField
import com.example.nexusbooking.mobile.ui.components.NexusTimePicker
import com.example.nexusbooking.mobile.ui.components.NexusTopAppBar
import com.example.nexusbooking.mobile.ui.navigation.HomeTab
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    initialTab: HomeTab,
    onNavigateToTab: (HomeTab) -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.error) {
        state.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error, duration = SnackbarDuration.Long)
                viewModel.clearMessages()
            }
        }
    }

    LaunchedEffect(state.success) {
        state.success?.let { success ->
            scope.launch {
                snackbarHostState.showSnackbar(success, duration = SnackbarDuration.Short)
                viewModel.clearMessages()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            NexusTopAppBar(
                title = "NexusBooking",
                actions = {
                    NexusIconButton(
                        icon = Icons.Default.Person,
                        contentDescription = "Perfil",
                        onClick = onOpenProfile
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Dashboard") },
                    label = { Text("Dashboard") },
                    selected = initialTab == HomeTab.DASHBOARD,
                    onClick = { onNavigateToTab(HomeTab.DASHBOARD) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, "Reservas") },
                    label = { Text("Reservas") },
                    selected = initialTab == HomeTab.BOOKINGS,
                    onClick = { onNavigateToTab(HomeTab.BOOKINGS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Favorite, "Grupos") },
                    label = { Text("Grupos") },
                    selected = initialTab == HomeTab.GROUPS,
                    onClick = { onNavigateToTab(HomeTab.GROUPS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, "Instalaciones") },
                    label = { Text("Instalaciones") },
                    selected = initialTab == HomeTab.FACILITIES,
                    onClick = { onNavigateToTab(HomeTab.FACILITIES) }
                )
                if (state.user?.role == "ADMIN") {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, "Admin") },
                        label = { Text("Admin") },
                        selected = initialTab == HomeTab.ADMIN,
                        onClick = { onNavigateToTab(HomeTab.ADMIN) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "¡Hola, ${state.user?.email ?: ""}!",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (initialTab) {
                HomeTab.DASHBOARD -> DashboardTab(state)
                HomeTab.FACILITIES -> FacilitiesTab(state)
                HomeTab.BOOKINGS -> BookingsTab(state, viewModel)
                HomeTab.GROUPS -> GroupsTab(state, viewModel)
                HomeTab.ADMIN -> AdminTab(state, viewModel)
            }
        }
    }
}

@Composable
private fun DashboardTab(state: HomeUiState) {
    androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Resumen", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "Instalaciones",
                            value = state.facilities.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Mis Reservas",
                            value = state.bookings.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Mis Grupos",
                            value = state.groups.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item {
            Text("Próximas Reservas", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        }

        if (state.bookings.isNotEmpty()) {
            items(state.bookings.take(3)) { booking ->
                NexusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(booking.facilityName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                            NexusStatusBadge(booking.status)
                        }
                        Text(booking.startTime.take(16), style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            item {
                NexusCard(modifier = Modifier.fillMaxWidth()) {
                    Text("No hay reservas próximas", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
            }
        }

        item {
            Text("Instalaciones Disponibles", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        }

        items(state.facilities.take(3)) { facility ->
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(facility.name, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                        NexusStatusBadge(facility.status)
                    }
                    Text("Cap: ${facility.capacity ?: 0} | Tipo: ${facility.type}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    NexusCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(value, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
            Text(label, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FacilitiesTab(state: HomeUiState) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.facilities) { facility ->
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(facility.name, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                        NexusStatusBadge(facility.status)
                    }
                    Text("Tipo: ${facility.type}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    Text("Capacidad: ${facility.capacity ?: 0} | Ubicación: ${facility.location ?: "-"}")
                }
            }
        }
    }
}

@Composable
private fun BookingsTab(state: HomeUiState, viewModel: HomeViewModel) {
    var showNewReservationForm by remember { mutableStateOf(false) }
    var selectedFacility by remember { mutableStateOf<FacilityResponse?>(null) }
    var selectedGroup by remember { mutableStateOf<GroupResponse?>(null) }
    var selectedDay by remember { mutableStateOf("") }
    var selectedStartTime by remember { mutableStateOf("10:00") }
    var selectedEndTime by remember { mutableStateOf("11:00") }
    var notes by remember { mutableStateOf("") }

    val activeFacilities = state.facilities.filter { it.status == "ACTIVE" }

    LaunchedEffect(activeFacilities, state.groups) {
        if (selectedFacility == null && activeFacilities.isNotEmpty()) {
            selectedFacility = activeFacilities.first()
        }
        if (selectedGroup == null && state.groups.isNotEmpty()) {
            selectedGroup = state.groups.first()
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            if (!showNewReservationForm) {
                NexusPrimaryButton(
                    text = "+ Nueva Reserva",
                    onClick = { showNewReservationForm = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = showNewReservationForm,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                NexusCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Nueva Reserva", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            androidx.compose.material3.IconButton(
                                onClick = { showNewReservationForm = false },
                                modifier = Modifier.weight(0.15f)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cerrar",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        NexusDropdown(
                            label = "Instalación",
                            selectedItem = selectedFacility,
                            items = activeFacilities,
                            onItemSelected = { selectedFacility = it },
                            itemLabel = { "${it.name} (${it.capacity} cap.)" },
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusDropdown(
                            label = "Grupo",
                            selectedItem = selectedGroup,
                            items = state.groups,
                            onItemSelected = { selectedGroup = it },
                            itemLabel = { it.name },
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusDatePicker(
                            label = "Día",
                            selectedDate = selectedDay,
                            onDateSelected = { selectedDay = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NexusTimePicker(
                                label = "Inicio",
                                onTimeSelected = { hour, minute ->
                                    selectedStartTime = String.format("%02d:%02d", hour, minute)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            NexusTimePicker(
                                label = "Fin",
                                onTimeSelected = { hour, minute ->
                                    selectedEndTime = String.format("%02d:%02d", hour, minute)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        NexusTextArea(
                            value = notes,
                            onValueChange = { notes = it },
                            label = "Notas",
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusPrimaryButton(
                            text = "Crear Reserva",
                            onClick = {
                                val fId = selectedFacility?.id ?: return@NexusPrimaryButton
                                val gId = selectedGroup?.id ?: return@NexusPrimaryButton
                                if (selectedDay.isEmpty()) return@NexusPrimaryButton

                                val startDateTime = "${selectedDay}T${selectedStartTime}:00.000Z"
                                val endDateTime = "${selectedDay}T${selectedEndTime}:00.000Z"
                                viewModel.createBooking(fId, gId, startDateTime, endDateTime, notes)
                                showNewReservationForm = false
                            }
                        )
                    }
                }
            }
        }

        items(state.bookings) { booking ->
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#${booking.id} - ${booking.facilityName}", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                        NexusStatusBadge(booking.status)
                    }
                    Text("${booking.startTime} → ${booking.endTime}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
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

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Crear Grupo", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    NexusTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nombre del grupo"
                    )
                    NexusTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Descripción"
                    )
                    NexusPrimaryButton(
                        text = "Crear Grupo",
                        onClick = { viewModel.createGroup(name, description) }
                    )
                }
            }
        }

        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Unirse a Grupo", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        NexusTextField(
                            value = joinCode,
                            onValueChange = { joinCode = it },
                            label = "Código del grupo",
                            modifier = Modifier.weight(1f)
                        )
                        NexusSecondaryButton(
                            text = "Unir",
                            onClick = {
                                if (joinCode.isNotBlank()) {
                                    viewModel.joinGroupByCode(joinCode)
                                }
                            },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }
        }

        items(state.groups) { group ->
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(group.name, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                    Text("${group.memberCount} miembros | Owner: ${group.ownerEmail}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    if (group.members.isNotEmpty()) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Miembros:", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                        group.members.take(4).forEach { member ->
                            Text("• ${member.email}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (state.user?.id != group.ownerId) {
                        NexusSecondaryButton(
                            text = "Salir del grupo",
                            onClick = { viewModel.leaveGroup(group.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTab(state: HomeUiState, viewModel: HomeViewModel) {
    var userId by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf("true") }
    var facilityName by remember { mutableStateOf("") }
    var facilityType by remember { mutableStateOf("") }
    var facilityCapacity by remember { mutableStateOf("") }
    var incidentTitle by remember { mutableStateOf("") }
    var incidentDescription by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Gestionar Usuarios", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    NexusTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = "ID Usuario"
                    )
                    NexusTextField(
                        value = isActive,
                        onValueChange = { isActive = it },
                        label = "Activo (true/false)"
                    )
                    NexusPrimaryButton(
                        text = "Actualizar Usuario",
                        onClick = {
                            val id = userId.toLongOrNull() ?: return@NexusPrimaryButton
                            val active = isActive.toBoolean()
                            viewModel.setUserActive(id, active)
                        }
                    )
                }
            }
        }

        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Crear Instalación", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    NexusTextField(
                        value = facilityName,
                        onValueChange = { facilityName = it },
                        label = "Nombre"
                    )
                    NexusTextField(
                        value = facilityType,
                        onValueChange = { facilityType = it },
                        label = "Tipo"
                    )
                    NexusTextField(
                        value = facilityCapacity,
                        onValueChange = { facilityCapacity = it },
                        label = "Capacidad"
                    )
                    NexusPrimaryButton(
                        text = "Crear Instalación",
                        onClick = {
                            val cap = facilityCapacity.toIntOrNull() ?: 0
                            viewModel.createFacility(facilityName, facilityType, cap)
                        }
                    )
                }
            }
        }

        item {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Crear Incidencia", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    NexusTextField(
                        value = incidentTitle,
                        onValueChange = { incidentTitle = it },
                        label = "Título"
                    )
                    NexusTextField(
                        value = incidentDescription,
                        onValueChange = { incidentDescription = it },
                        label = "Descripción"
                    )
                    NexusPrimaryButton(
                        text = "Crear Incidencia",
                        onClick = { viewModel.createIncident(incidentTitle, incidentDescription) }
                    )
                }
            }
        }

        item {
            Text("Usuarios del Sistema", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
        }

        items(state.users) { user ->
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(user.email, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        NexusStatusBadge(if (user.active) "ACTIVO" else "INACTIVO")
                    }
                    Text("${user.role} | #${user.id}", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
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
