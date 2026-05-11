package com.example.nexusbooking.mobile.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nexusbooking.mobile.ui.theme.NexusBlueDark
import com.example.nexusbooking.mobile.ui.theme.NexusBluePrimary
import com.example.nexusbooking.mobile.R
import com.example.nexusbooking.mobile.data.remote.dto.BookingResponse
import com.example.nexusbooking.mobile.data.remote.dto.FacilityResponse
import com.example.nexusbooking.mobile.data.remote.dto.GroupResponse
import com.example.nexusbooking.mobile.ui.components.CalendarView
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
                titleContent = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nexus_logo_nobg),
                            contentDescription = "Nexus Logo",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Fit
                        )
                        val titleText = buildAnnotatedString {
                            withStyle(SpanStyle(color = NexusBlueDark)) {
                                append("Nexus")
                            }
                            withStyle(SpanStyle(color = NexusBluePrimary)) {
                                append("Booking")
                            }
                        }
                        Text(
                            text = titleText,
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    NexusIconButton(
                        icon = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile_button),
                        onClick = onOpenProfile
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
                    selected = initialTab == HomeTab.DASHBOARD,
                    onClick = { onNavigateToTab(HomeTab.DASHBOARD) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, stringResource(R.string.nav_bookings)) },
                    label = { Text(stringResource(R.string.nav_bookings)) },
                    selected = initialTab == HomeTab.BOOKINGS,
                    onClick = { onNavigateToTab(HomeTab.BOOKINGS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Group, stringResource(R.string.nav_groups)) },
                    label = { Text(stringResource(R.string.nav_groups)) },
                    selected = initialTab == HomeTab.GROUPS,
                    onClick = { onNavigateToTab(HomeTab.GROUPS) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.LocationOn, stringResource(R.string.nav_facilities)) },
                    label = { Text(stringResource(R.string.nav_facilities)) },
                    selected = initialTab == HomeTab.FACILITIES,
                    onClick = { onNavigateToTab(HomeTab.FACILITIES) }
                )
                if (state.user?.role == "ADMIN") {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, stringResource(R.string.nav_admin)) },
                        label = { Text(stringResource(R.string.nav_admin)) },
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
            val pageTitle = when (initialTab) {
                HomeTab.DASHBOARD -> stringResource(R.string.greeting_hello, state.user?.email ?: "")
                HomeTab.BOOKINGS -> "${stringResource(R.string.my_bookings)} (${state.bookings.size})"
                HomeTab.GROUPS -> "${stringResource(R.string.my_groups)} (${state.groups.size})"
                HomeTab.FACILITIES -> "${stringResource(R.string.available_facilities)} (${state.facilities.size})"
                HomeTab.ADMIN -> stringResource(R.string.admin_panel)
            }

            Text(
                pageTitle,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (initialTab) {
                HomeTab.DASHBOARD -> DashboardTab(state, onNavigateToTab)
                HomeTab.FACILITIES -> FacilitiesTab(state)
                HomeTab.BOOKINGS -> BookingsTab(state, viewModel)
                HomeTab.GROUPS -> GroupsTab(state, viewModel)
                HomeTab.ADMIN -> AdminTab(state, viewModel)
            }
        }
    }
}

@Composable
private fun DashboardTab(state: HomeUiState, onNavigateToTab: (HomeTab) -> Unit) {
    var selectedDayBookings by remember { mutableStateOf<List<BookingResponse>>(emptyList()) }

    androidx.compose.foundation.lazy.LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.dashboard_summary), style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
//                        StatCard(
//                            label = stringResource(R.string.facilities_count),
//                            value = state.facilities.size.toString(),
//                            modifier = Modifier.weight(1f)
//                        )
                        StatCard(
                            label = stringResource(R.string.my_bookings_count),
                            value = state.bookings.size.toString(),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(HomeTab.BOOKINGS) }
                        )
                        StatCard(
                            label = stringResource(R.string.my_groups_count),
                            value = state.groups.size.toString(),
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTab(HomeTab.GROUPS) }
                        )
                    }
                }
            }
        }

        item {
            CalendarView(
                bookings = state.bookings,
                onDaySelected = { _, bookings -> selectedDayBookings = bookings },
                onMonthChanged = { selectedDayBookings = emptyList() }
            )
        }

        if (selectedDayBookings.isNotEmpty()) {
            items(selectedDayBookings) { booking ->
                NexusCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text("#${booking.id}", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                                Text(booking.facilityName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                            }
                            NexusStatusBadge(booking.status)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            Text(booking.groupName ?: "-", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            val startDateTime = try {
                                java.time.LocalDateTime.parse(booking.startTime, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                            } catch (e: Exception) {
                                null
                            }
                            val formattedDate = startDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: booking.startTime.take(10)
                            val startHour = startDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) ?: booking.startTime.substring(11, 16)
                            val endHour = try {
                                java.time.LocalDateTime.parse(booking.endTime, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            } catch (e: Exception) {
                                booking.endTime.substring(11, 16)
                            }

                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            Text(formattedDate, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                            Text("$startHour → $endHour", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        } else if (state.bookings.isNotEmpty()) {
            item {
                NexusCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Selecciona un día con reservas para ver detalles", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            item {
                NexusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.no_upcoming_bookings), style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val cardModifier = if (onClick != null) {
        modifier.then(Modifier.clickable { onClick() })
    } else {
        modifier
    }
    NexusCard(modifier = cardModifier) {
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
                            Text(stringResource(R.string.new_booking_title), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
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
                            label = stringResource(R.string.facility_label),
                            selectedItem = selectedFacility,
                            items = activeFacilities,
                            onItemSelected = { selectedFacility = it },
                            itemLabel = { "${it.name} (${it.capacity} cap.)" },
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusDropdown(
                            label = stringResource(R.string.group_label),
                            selectedItem = selectedGroup,
                            items = state.groups,
                            onItemSelected = { selectedGroup = it },
                            itemLabel = { it.name },
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusDatePicker(
                            label = stringResource(R.string.day_label),
                            selectedDate = selectedDay,
                            onDateSelected = { selectedDay = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NexusTimePicker(
                                label = stringResource(R.string.start_time_label),
                                onTimeSelected = { hour, minute ->
                                    selectedStartTime = String.format("%02d:%02d", hour, minute)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            NexusTimePicker(
                                label = stringResource(R.string.end_time_label),
                                onTimeSelected = { hour, minute ->
                                    selectedEndTime = String.format("%02d:%02d", hour, minute)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        NexusTextArea(
                            value = notes,
                            onValueChange = { notes = it },
                            label = stringResource(R.string.notes_label),
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusPrimaryButton(
                            text = stringResource(R.string.create_booking_button),
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
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("#${booking.id}", style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                            Text(booking.facilityName, style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
                        }
                        NexusStatusBadge(booking.status)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        Text(booking.groupName ?: "-", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        val startDateTime = try {
                            java.time.LocalDateTime.parse(booking.startTime, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                        } catch (e: Exception) {
                            null
                        }
                        val formattedDate = startDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: booking.startTime.take(10)
                        val startHour = startDateTime?.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) ?: booking.startTime.substring(11, 16)
                        val endHour = try {
                            java.time.LocalDateTime.parse(booking.endTime, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                        } catch (e: Exception) {
                            booking.endTime.substring(11, 16)
                        }

                        Text(formattedDate, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                        Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        Text("$startHour → $endHour", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun GroupsTab(state: HomeUiState, viewModel: HomeViewModel) {
    var showCreateGroupForm by remember { mutableStateOf(false) }
    var showJoinGroupForm by remember { mutableStateOf(false) }
    var editingGroupId by remember { mutableStateOf<Long?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGroupToJoin by remember { mutableStateOf<GroupResponse?>(null) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NexusPrimaryButton(
                    text = "+ ${stringResource(R.string.create_group_title)}",
                    onClick = { showCreateGroupForm = true },
                    modifier = Modifier.weight(1f)
                )
                NexusPrimaryButton(
                    text = "+ ${stringResource(R.string.join_group_title)}",
                    onClick = { showJoinGroupForm = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = showCreateGroupForm,
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
                            Text(stringResource(R.string.create_group_title), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            androidx.compose.material3.IconButton(
                                onClick = { showCreateGroupForm = false },
                                modifier = Modifier.weight(0.15f)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close_button),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        NexusTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = stringResource(R.string.group_name_label)
                        )
                        NexusTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = stringResource(R.string.description_label)
                        )
                        NexusPrimaryButton(
                            text = stringResource(R.string.create_group_button),
                            onClick = {
                                viewModel.createGroup(name, description)
                                showCreateGroupForm = false
                                name = ""
                                description = ""
                            }
                        )
                    }
                }
            }
        }

        item {
            AnimatedVisibility(
                visible = showJoinGroupForm,
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
                            Text(stringResource(R.string.join_group_title), style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            androidx.compose.material3.IconButton(
                                onClick = { showJoinGroupForm = false },
                                modifier = Modifier.weight(0.15f)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close_button),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        NexusDropdown(
                            label = stringResource(R.string.select_group_label),
                            selectedItem = selectedGroupToJoin,
                            items = state.groups,
                            onItemSelected = { selectedGroupToJoin = it },
                            itemLabel = { group ->
                                "${group.name} - ${group.description ?: ""} (${group.memberCount} miembros)"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusPrimaryButton(
                            text = stringResource(R.string.join_button),
                            onClick = {
                                selectedGroupToJoin?.let { group ->
                                    viewModel.joinGroup(group.id)
                                    selectedGroupToJoin = null
                                    showJoinGroupForm = false
                                }
                            },
                            enabled = selectedGroupToJoin != null
                        )
                    }
                }
            }
        }

        items(state.groups) { group ->
            AnimatedVisibility(
                visible = editingGroupId == group.id,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                var editName by remember(group.id) { mutableStateOf(group.name) }
                var editDescription by remember(group.id) { mutableStateOf(group.description ?: "") }

                NexusCard {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text("Editar Grupo", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            androidx.compose.material3.IconButton(
                                onClick = { editingGroupId = null },
                                modifier = Modifier.weight(0.15f)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.close_button),
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        NexusTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = stringResource(R.string.group_name_label),
                            modifier = Modifier.fillMaxWidth()
                        )
                        NexusTextField(
                            value = editDescription,
                            onValueChange = { editDescription = it },
                            label = stringResource(R.string.description_label),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NexusSecondaryButton(
                                text = stringResource(R.string.cancel_button),
                                onClick = { editingGroupId = null },
                                modifier = Modifier.weight(1f)
                            )
                            NexusPrimaryButton(
                                text = stringResource(R.string.save_button),
                                onClick = {
                                    // TODO: Implement updateGroup in backend and viewModel
                                    editingGroupId = null
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        items(state.groups) { group ->
            var showMembers by remember(group.id) { mutableStateOf(false) }

            NexusCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text(group.name, style = androidx.compose.material3.MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (state.user?.id == group.ownerId) {
                                androidx.compose.material3.IconButton(onClick = { editingGroupId = group.id }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar grupo", modifier = Modifier.size(20.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                                }
                            }
                            if (state.user?.id != group.ownerId) {
                                androidx.compose.material3.IconButton(onClick = { viewModel.leaveGroup(group.id) }) {
                                    Icon(Icons.Default.Logout, contentDescription = "Salir del grupo", modifier = Modifier.size(20.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    if (!group.description.isNullOrEmpty()) {
                        Text(group.description, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMembers = !showMembers },
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp), tint = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                        Text("${group.memberCount} miembros", style = androidx.compose.material3.MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
                        Text("| Owner: ${group.ownerEmail}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    }
                    AnimatedVisibility(visible = showMembers) {
                        if (group.members.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Text("Miembros:", style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
                                group.members.take(4).forEach { member ->
                                    Text("• ${member.email}", style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
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
