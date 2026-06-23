package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.Screen
import com.example.TaxiViewModel
import com.example.data.DriverRegistrationEntity
import com.example.data.TaxiOrderEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TaxiApp(viewModel: TaxiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    Screen.Welcome -> WelcomeScreen(
                        onSelectPassenger = { viewModel.navigateTo(Screen.PassengerAuth) },
                        onSelectDriver = { viewModel.navigateTo(Screen.DriverOnboarding) },
                        onSelectAdmin = { viewModel.navigateTo(Screen.AdminPanel) },
                        viewModel = viewModel
                    )
                    Screen.PassengerAuth -> PassengerAuthScreen(viewModel)
                    Screen.PassengerHome -> PassengerHomeScreen(viewModel)
                    Screen.DriverOnboarding -> DriverOnboardingScreen(viewModel)
                    Screen.DriverWaitingApproval -> DriverWaitingScreen(viewModel)
                    Screen.DriverHome -> DriverHomeScreen(viewModel)
                    Screen.AdminPanel -> AdminPanelScreen(viewModel)
                }
            }

            // Developer Quick Swapper Drawer/Indicator on screens other than Welcome to easily test passenger-driver roles
            if (currentScreen != Screen.Welcome) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                ) {
                    DevQuickSwapper(
                        currentRole = currentUser?.role ?: "Қонақ",
                        onLogout = { viewModel.logout() },
                        onGoToAdmin = { viewModel.navigateTo(Screen.AdminPanel) }
                    )
                }
            }
        }
    }
}

// ---------------- WELCOME ENTRY SCREEN ----------------
@Composable
fun WelcomeScreen(
    onSelectPassenger: () -> Unit,
    onSelectDriver: () -> Unit,
    onSelectAdmin: () -> Unit,
    viewModel: TaxiViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF101012),
                        Color(0xFF1B1B1F)
                    )
                )
            )
    ) {
        // Subtle decorative grid-like backings & shapes representing Kazakh ornament and taxi theme
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ornamentBrush = Brush.radialGradient(
                colors = listOf(TaxiGold.copy(alpha = 0.08f), Color.Transparent),
                center = Offset(size.width / 2, size.height / 3),
                radius = size.width * 0.8f
            )
            drawCircle(
                brush = ornamentBrush,
                center = Offset(size.width / 2, size.height / 3),
                radius = size.width * 0.8f
            )

            // Draw abstract road vectors
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(size.width * 0.1f, 0f),
                end = Offset(size.width * 0.1f, size.height),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(size.width * 0.9f, 0f),
                end = Offset(size.width * 0.9f, size.height),
                strokeWidth = 3f
            )
            drawLine(
                color = TaxiGold.copy(alpha = 0.12f),
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 4f,
                pathEffect = pathEffect
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header spacing padding
            Spacer(modifier = Modifier.height(16.dp))

            // Branding element
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.animateContentSize()
            ) {
                // Large Gold Logo Icon or National Pattern Logo
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(26.dp))
                        .background(TaxiGold)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalTaxi,
                        contentDescription = "Орда лого",
                        tint = DarkSlate,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "ОРДА ТАКСИ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TaxiGold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Жылдам әрі сенімді қызмет",
                    fontSize = 15.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom CTA Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Қосымшаға кіру үшін рөлді таңдаңыз:",
                    fontSize = 14.sp,
                    color = TextWhite.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = onSelectPassenger,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TaxiGold,
                        contentColor = DarkSlate
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("welcome_passenger_button"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = DarkSlate)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Жолаушы",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onSelectDriver,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = TaxiGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, TaxiGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("welcome_driver_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null, tint = TaxiGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Жүргізуші",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Under-text Slogan phrase / info helper
                Row(
                    modifier = Modifier.clickable { onSelectAdmin() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Орда бақылау жүйесі",
                        fontSize = 12.sp,
                        color = TextGray.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = TextGray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

// ---------------- PASSENGER AUTHENTICATION/REGISTRATION SCREEN ----------------
@Composable
fun PassengerAuthScreen(viewModel: TaxiViewModel) {
    val nameInput by viewModel.passNameInput.collectAsStateWithLifecycle()
    val phoneInput by viewModel.passPhoneInput.collectAsStateWithLifecycle()
    val passwordInput by viewModel.passPasswordInput.collectAsStateWithLifecycle()
    val error by viewModel.passAuthError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    modifier = Modifier.background(SurfaceDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Артқа",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Жолаушы тіркелуі",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Icon decorative banner
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = TaxiGold,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "Номер арқылы кіріңіз немесе тіркеліңіз",
                fontSize = 14.sp,
                color = TextGray,
                modifier = Modifier.padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error ?: "",
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Input Column
            OutlinedTextField(
                value = nameInput,
                onValueChange = { viewModel.passNameInput.value = it },
                label = { Text("Аты-жөніңіз (Аты дон)") },
                placeholder = { Text("Мысалы: Дархан") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("passenger_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = phoneInput,
                onValueChange = { viewModel.passPhoneInput.value = it },
                label = { Text("Телефон номеріңіз") },
                placeholder = { Text("+7 (___) ___-__-__") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("passenger_phone_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { viewModel.passPasswordInput.value = it },
                label = { Text("Құпия сөз (Жаңа пароль)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .testTag("passenger_password_input"),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Button(
                onClick = { viewModel.submitPassengerAuth() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TaxiGold,
                    contentColor = DarkSlate
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_passenger_auth"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Тіркелу / Кіру",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------------- PASSENGER HOME SCREEN WITH MAP IMPLEMENTATION ----------------
@Composable
fun PassengerHomeScreen(viewModel: TaxiViewModel) {
    val pickup by viewModel.pickupAddress.collectAsStateWithLifecycle()
    val dest by viewModel.destinationAddress.collectAsStateWithLifecycle()
    val tariff by viewModel.selectedTariff.collectAsStateWithLifecycle()
    val price by viewModel.ridePrice.collectAsStateWithLifecycle()
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeOrder by viewModel.activePassengerOrder.collectAsStateWithLifecycle()
    val error by viewModel.orderBookingError.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val history by viewModel.passengerOrdersHistory.collectAsStateWithLifecycle(initialValue = emptyList())
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = DarkSlate,
                drawerContentColor = TextWhite,
                modifier = Modifier.width(300.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header banner
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(TaxiGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalTaxi,
                                contentDescription = null,
                                tint = DarkSlate,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Орда Такси",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TaxiGold
                            )
                            Text(
                                text = "Жолаушы мәзірі",
                                fontSize = 11.sp,
                                color = TextGray
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(bottom = 16.dp))

                    // Menu item 1: Trip History (Поездка тарихы)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.History, contentDescription = null, tint = TaxiGold) },
                        label = { Text("Поездка тарихы", fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showHistoryDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TaxiGold,
                            unselectedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Menu item 2: Support Contact (Падержка)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = TaxiGold) },
                        label = { Text("Қолдау қызметі (Падержка)", fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showSupportDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TaxiGold,
                            unselectedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Menu item 3: Settings (Настройка)
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = TaxiGold) },
                        label = { Text("Баптаулар (Настройка)", fontWeight = FontWeight.Bold) },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showSettingsDialog = true
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = Color.Transparent,
                            unselectedIconColor = TaxiGold,
                            unselectedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(bottom = 16.dp))

                    // Logout/Exit button
                    Button(
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            viewModel.logout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Шығу (Басты бетке)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSlate)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalTaxi,
                            contentDescription = null,
                            tint = TaxiGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Орда",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = TaxiGold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user?.name ?: "Жолаушы",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(TaxiGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.name ?: "Ж").take(1).uppercase(),
                                color = DarkSlate,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Screen State split: Active Ride Tracking vs Booking Console
                if (activeOrder != null) {
                    // RIDE IN PROGRESS WINDOW
                    ActiveTripPassengerView(activeOrder!!, viewModel)
                } else {
                    // ACTIVE BOOKING WORKFLOW (MAP INCLUDED)
                    Box(modifier = Modifier.weight(1f)) {
                        // Beautiful simulated interactive Map Canvas
                        TaxiInteractiveMap(
                            pickupAddress = pickup,
                            destAddress = dest,
                            durationMinutes = viewModel.predictedDurationMinutes.collectAsStateWithLifecycle().value,
                            onMenuClick = {
                                coroutineScope.launch {
                                    drawerState.open()
                                }
                            },
                            onMapTapPlace = { pickedAdd ->
                                // Update Destination address when tapping interactive map
                                viewModel.destinationAddress.value = pickedAdd
                                viewModel.calculatePriceOfRide()
                            }
                        )

                        // Booking address fields floating card (Like shown in the image illustration)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .align(Alignment.BottomCenter),
                            colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.95f)),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Pickup Field
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(TaxiGold, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    OutlinedTextField(
                                        value = pickup,
                                        onValueChange = {
                                            viewModel.pickupAddress.value = it
                                            viewModel.calculatePriceOfRide()
                                        },
                                        placeholder = { Text("Қайдан кетесіз?", color = TextGray) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("pickup_input"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent
                                        ),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                                    )
                                }

                                Divider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(vertical = 4.dp))

                                // Destination Field
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.LightGray, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    OutlinedTextField(
                                        value = dest,
                                        onValueChange = {
                                            viewModel.destinationAddress.value = it
                                            viewModel.calculatePriceOfRide()
                                        },
                                        placeholder = { Text("Қайда барасыз?", color = TextGray) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("destination_input"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite,
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent
                                        ),
                                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                                    )
                                    if (dest.isNotEmpty()) {
                                        IconButton(onClick = {
                                            viewModel.destinationAddress.value = ""
                                            viewModel.ridePrice.value = 0.0
                                        }) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = "Өшіру", tint = TextGray)
                                        }
                                    }
                                }

                                // Dynamic Landmarks Suggestions helper
                                AnimatedVisibility(visible = dest.isEmpty()) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        Text(
                                            text = "Тез таңдау:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TaxiGold,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                        val locations = listOf(
                                            "Бәйтерек монументі",
                                            "Астана Арена стадионы",
                                            "Медеу мұз айдыны",
                                            "Достық даңғылы, 10"
                                        )
                                        Row(
                                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            locations.forEach { location ->
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                                        .clickable {
                                                            viewModel.destinationAddress.value = location
                                                            viewModel.calculatePriceOfRide()
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                                ) {
                                                    Text(text = location, fontSize = 11.sp, color = TextWhite)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Booking Error banner
                                if (error != null) {
                                    Text(
                                        text = error ?: "",
                                        color = ErrorRed,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }

                                // Category tariff selection: Standard ONLY is requested ("Стандартты санат болсын, бизнес комфорт керек емес")
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                                        .border(1.5.dp, TaxiGold, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(TaxiGold.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DirectionsCar,
                                                contentDescription = null,
                                                tint = TaxiGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "Стандарт",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = "Шағын мерзімде келеді",
                                                fontSize = 11.sp,
                                                color = TextGray
                                            )
                                        }
                                    }

                                    if (price > 0.0) {
                                        Text(
                                            text = "${price.toInt()} ₸",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = TaxiGold
                                        )
                                    } else {
                                        Text(
                                            text = "Баға анықталуда",
                                            fontSize = 11.sp,
                                            color = TextGray
                                        )
                                    }
                                }

                                // CTA Book Ride action button (Shown once target is set)
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.bookRide() },
                                    enabled = dest.isNotEmpty() && price > 0.0,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("book_taxi_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = TaxiGold,
                                        disabledContainerColor = Color.White.copy(alpha = 0.1f),
                                        contentColor = DarkSlate,
                                        disabledContentColor = TextGray
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = if (dest.isEmpty()) "Баратын жерді жазыңыз" else "Тапсырыс беру",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialogs
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = TaxiGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Сапарлар тарихы", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("Сізде әлі сапарлар жоқ.", color = TextGray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(history) { order ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSlate),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Стандарт санат", fontSize = 11.sp, color = TaxiGold, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = "Аяқталды",
                                            fontSize = 11.sp,
                                            color = SuccessGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "📍 Басы: ${order.pickupAddress}",
                                        fontSize = 13.sp,
                                        color = TextWhite,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "🏁 Соңы: ${order.destinationAddress}",
                                        fontSize = 13.sp,
                                        color = TextWhite,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Жүргізуші: ${order.driverName ?: "Белгісіз"}", fontSize = 12.sp, color = TextGray)
                                        Text(text = "${order.price.toInt()} ₸", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TaxiGold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHistoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate)
                ) {
                    Text("Жабу", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = TaxiGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Қолдау қызметі", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Орда Такси желісі тәулік бойы сізге көмектесуге дайын!", color = TextWhite, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📞 Телефон нөмірі: +7 (707) 111-22-33", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("📧 Электронды пошта: support@ordataxi.kz", color = TextWhite, fontSize = 14.sp)
                    Text("📍 Мекен-жайы: Қазақстан, Астана қ.", color = TextGray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSupportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate)
                ) {
                    Text("Жабу", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = SurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = TaxiGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Баптаулар (Настройка)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Қосымша баптаулары:", color = TaxiGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Дыбыстық хабарламалар", color = TextWhite)
                        Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = TaxiGold, checkedTrackColor = TaxiGold.copy(alpha = 0.5f)))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Түнгі режим (Dark slate)", color = TextWhite)
                        Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = TaxiGold, checkedTrackColor = TaxiGold.copy(alpha = 0.5f)))
                    }
                    Text("Қазіргі нұсқасы: v1.0.4 (Орда Такси)", color = TextGray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate)
                ) {
                    Text("Сақтау", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// ---------------- PASSENGER ACTIVE TRIP STATE PANEL ----------------
@Composable
fun ActiveTripPassengerView(order: TaxiOrderEntity, viewModel: TaxiViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Тапсырыс статусы",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Text(
                    text = when (order.status) {
                        "CREATED" -> "Жүргізуші ізделуде..."
                        "ACCEPTED" -> "Жүргізуші қабылдады!"
                        "ARRIVED" -> "Жүргізуші келді!"
                        "STARTED" -> "Сапар жолында..."
                        "COMPLETED" -> "Сапар аяқталды!"
                        else -> "Жолда"
                    },
                    color = TaxiGold,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Visual State Indicators (Glow search circles or Driver Details)
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (order.status == "CREATED") {
                    // Pulsing search animation
                    var scale by remember { mutableStateOf(1f) }
                    LaunchedEffect(key1 = true) {
                        while (true) {
                            scale = 1.3f
                            delay(1000)
                            scale = 1f
                            delay(1000)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(TaxiGold.copy(alpha = 0.1f * scale), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(TaxiGold.copy(alpha = 0.15f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(TaxiGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = DarkSlate,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                } else {
                    // Driver Card display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SurfaceDark, RoundedCornerShape(110.dp))
                            .border(2.dp, TaxiGold, RoundedCornerShape(110.dp))
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(TaxiGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = DarkSlate,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = order.driverName ?: "Танылмаған жүргізуші",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        TagGold(text = order.carBrandModel ?: "Машина анықталмаған")
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = TaxiGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "4.9", fontSize = 12.sp, color = TextGray)
                        }
                    }
                }
            }

            // Route Breakdown Details
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    RouteLegView(label = "Қайдан (Pickup):", name = order.pickupAddress)
                    Spacer(modifier = Modifier.height(10.dp))
                    RouteLegView(label = "Қайда (Destination):", name = order.destinationAddress)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Жол бағасы:", fontSize = 13.sp, color = TextGray)
                        Text(
                            text = "${order.price.toInt()} ₸",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TaxiGold
                        )
                    }
                }
            }

            // Bottom Flow controllers
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (order.status == "CREATED") {
                    // Assist Simulator toggle for faster manual tests: Drivers can pick this order directly,
                    // but we can also manually trigger simulation steps so user can test the cycle in 1 click!
                    Text(
                        text = "Сынақ нұсқа: Жүргізушіні автоматты орындау",
                        fontSize = 11.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Direct order update simulated
                                val updated = order.copy(
                                    status = "ACCEPTED",
                                    driverPhone = "87071112233",
                                    driverName = "Марат (Темір тұлпар)",
                                    carBrandModel = "Hyundai Accent, 02 KZ 555"
                                )
                                viewModel.updateOrder(updated)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TaxiGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, TaxiGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Жүргізуші болып қабылдау (Сим.)", fontSize = 13.sp)
                    }
                } else if (order.status == "ACCEPTED") {
                    Button(
                        onClick = { viewModel.driverArrived(order.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Жүргізуші келді деп белгілеу (Сим.)")
                    }
                } else if (order.status == "ARRIVED") {
                    Button(
                        onClick = { viewModel.startTrip(order.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Сапарды бастау (Сим.)")
                    }
                } else if (order.status == "STARTED") {
                    Button(
                        onClick = { viewModel.completeTrip(order.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Сапарды аяқтау (Сим.)")
                    }
                } else if (order.status == "COMPLETED") {
                    // Show final Rating dialog or stars
                    Text(
                        text = "Сапар сәтті аяқталды! Жүргізушіге баға беріңіз:",
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    var selectedStars by remember { mutableStateOf(5) }
                    Row(
                        modifier = Modifier.padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i <= selectedStars) TaxiGold else Color.LightGray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable { selectedStars = i }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.finishOrderRating(order.id)
                            coroutineScope.launch {
                                // Return back to search state
                                val finished = order.copy(status = "CANCELLED") // removes from active list
                                viewModel.updateOrder(finished)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Бағалау және аяқтау", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------- DRIVER REGISTRATION/ONBOARDING SCREEN ----------------
@Composable
fun DriverOnboardingScreen(viewModel: TaxiViewModel) {
    val nameInput by viewModel.driverNameInput.collectAsStateWithLifecycle()
    val phoneInput by viewModel.driverPhoneInput.collectAsStateWithLifecycle()
    val passwordInput by viewModel.driverPasswordInput.collectAsStateWithLifecycle()
    val carInput by viewModel.driverCarBrandModel.collectAsStateWithLifecycle()

    val licenseFile by viewModel.driverLicenseText.collectAsStateWithLifecycle()
    val idFile by viewModel.driverIdText.collectAsStateWithLifecycle()
    val carPhoto by viewModel.driverCarPhotoText.collectAsStateWithLifecycle()
    val selfieFile by viewModel.driverSelfieText.collectAsStateWithLifecycle()

    val error by viewModel.driverAuthError.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    modifier = Modifier.background(SurfaceDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Артқа",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Жүргізуші тіркелуі",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Text(
                text = "Толық тіркелу арқылы Орда такси драйвері болыңыз",
                fontSize = 13.sp,
                color = TextGray,
                modifier = Modifier.padding(bottom = 20.dp),
                textAlign = TextAlign.Center
            )

            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = error ?: "",
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Input Fields
            OutlinedTextField(
                value = nameInput,
                onValueChange = { viewModel.driverNameInput.value = it },
                label = { Text("Толық аты-жөніңіз") },
                placeholder = { Text("Мысалы: Бауыржан") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("driver_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = phoneInput,
                onValueChange = { viewModel.driverPhoneInput.value = it },
                label = { Text("Қосылатын телефон номеріңіз") },
                placeholder = { Text("+7 (777) 123-45-67") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("driver_phone_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = passwordInput,
                onValueChange = { viewModel.driverPasswordInput.value = it },
                label = { Text("Құпия сөз құрастырыңыз") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .testTag("driver_password_input"),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = carInput,
                onValueChange = { viewModel.driverCarBrandModel.value = it },
                label = { Text("Машина маркасы мен моделі және нөмірі") },
                placeholder = { Text("Toyota Camry, 01 XYZ 77") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .testTag("driver_car_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = TaxiGold,
                    unfocusedBorderColor = TextGray,
                    focusedLabelColor = TaxiGold,
                    unfocusedLabelColor = TextGray
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Documents uploading files header
            Text(
                text = "Құжаттарды жүктеу (Міндетті түрде):",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TaxiGold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 12.dp)
            )

            // Document Pickers grid representation
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MockFilePickerRow(
                    label = "Жүргізуші куәлігі (Права)",
                    fileName = licenseFile,
                    onPick = { viewModel.driverLicenseText.value = "Куәлік_${Random.nextInt(100, 999)}_жүктелді.png" }
                )
                MockFilePickerRow(
                    label = "Жеке куәлік (Удостоверение)",
                    fileName = idFile,
                    onPick = { viewModel.driverIdText.value = "Удост_${Random.nextInt(100, 999)}_жүктелді.png" }
                )
                MockFilePickerRow(
                    label = "Машина фотосы (Төрт жағынан)",
                    fileName = carPhoto,
                    onPick = { viewModel.driverCarPhotoText.value = "Көлік_${Random.nextInt(100, 999)}_жүктелді.png" }
                )
                MockFilePickerRow(
                    label = "Куәлікпен фото (Селфи правамен)",
                    fileName = selfieFile,
                    onPick = { viewModel.driverSelfieText.value = "Селфи_куәлікпен_${Random.nextInt(100, 999)}.png" }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { viewModel.submitDriverOnboarding() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = TaxiGold,
                    contentColor = DarkSlate
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_driver_auth"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Тіркелуге өтінім беру",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ---------------- DRIVER WAITING SCREEN ----------------
@Composable
fun DriverWaitingScreen(viewModel: TaxiViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val driverProfile by viewModel.curDriverProfile.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(TaxiGold.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Күту",
                        tint = TaxiGold,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Мақұлдау күтілуде",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Супер! Тіркелген құжаттарыңыз админмен тексерілуде. Админ қабылдаған соң линияға шығу мүмкіндігі ашылады.",
                    fontSize = 14.sp,
                    color = TextGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Өтінім мәліметтері:", fontSize = 12.sp, color = TaxiGold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Аты: ${driverProfile?.name ?: user?.name}", fontSize = 13.sp, color = TextWhite)
                        Text(text = "Көлік: ${driverProfile?.carBrandModel ?: "Марка"}", fontSize = 13.sp, color = TextWhite)
                        Text(
                            text = "Статус: ${
                                when (driverProfile?.status) {
                                    "APPROVED" -> "МАҚҰЛДАНДЫ (Кіре аласыз!)"
                                    "REJECTED" -> "ҚАБЫЛДАНБАДЫ"
                                    else -> "КҮТУДЕ (PENDING)"
                                }
                            }",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (driverProfile?.status == "APPROVED") SuccessGreen else TaxiGold
                        )
                    }
                }
            }

            // Quick actions simulator for testing so users don't get locked out
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Сынақ тестілеушілері үшін құжаттарды жылдам қабылдау",
                    fontSize = 11.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.adminApproveDriver(user?.phone ?: "")
                            // Directly route after state change
                            viewModel.navigateTo(Screen.DriverHome)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Мақұлдау", fontSize = 12.sp)
                    }

                    Button(
                        onClick = { viewModel.adminRejectDriver(user?.phone ?: "") },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Қабылдамау", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TextWhite),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Артқа шығу")
                }
            }
        }
    }
}

// ---------------- DRIVER HOME DASHBOARD (ONLINE/OFFLINE TOGGLE) ----------------
@Composable
fun DriverHomeScreen(viewModel: TaxiViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val driverProfile by viewModel.curDriverProfile.collectAsStateWithLifecycle()
    val availableRides by viewModel.availableOrders.collectAsStateWithLifecycle()
    val activeRide by viewModel.activeDriverOrder.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                if (driverProfile?.isOnline == true) SuccessGreen else Color.LightGray,
                                CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (driverProfile?.isOnline == true) "Линияда (ONLINE)" else "Линиядан тыс (OFFLINE)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (driverProfile?.isOnline == true) SuccessGreen else TextGray
                    )
                }

                Switch(
                    checked = driverProfile?.isOnline ?: false,
                    onCheckedChange = { isOnline ->
                        viewModel.toggleDriverOnline(isOnline)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SuccessGreen,
                        checkedTrackColor = SuccessGreen.copy(alpha = 0.4f),
                        uncheckedThumbColor = Color.DarkGray,
                        uncheckedTrackColor = Color.LightGray.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("online_offline_switch")
                )
            }

            if (activeRide != null) {
                // ACTIVE WORKING TRIP PANEL
                ActiveTripDriverView(activeRide!!, viewModel)
            } else {
                // NORMAL DASHBOARD
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Driver summary panel
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = driverProfile?.name ?: user?.name ?: "Драйвер", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text(text = driverProfile?.carBrandModel ?: "Марка", fontSize = 12.sp, color = TextGray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Табыс", fontSize = 10.sp, color = TextGray)
                                Text(text = "14,500 ₸", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TaxiGold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (driverProfile?.isOnline == false) {
                        // OFFLINE VIEW STATE
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AirportShuttle,
                                contentDescription = null,
                                tint = TextGray.copy(alpha = 0.2f),
                                modifier = Modifier.size(120.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Баланс толтырылып, линиядасыз ба?",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Text(
                                text = "Тапсырыс алу үшін линия қосқышын басыңыз.",
                                fontSize = 13.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } else {
                        // ONLINE VIEW STATE (Show available orders)
                        Text(
                            text = "Қолжетімді тапсырыстар (${availableRides.size}):",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiGold,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 12.dp)
                        )

                        if (availableRides.isEmpty()) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = TaxiGold, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Жақын маңда ашық тапсырыс жоқ...", fontSize = 14.sp, color = TextGray)
                                Text(
                                    text = "Бірдеңе өзгергенде осында шығады.",
                                    fontSize = 11.sp,
                                    color = TextGray
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(availableRides.size) { index ->
                                    val order = availableRides[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth().testTag("ride_offer_item"),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TagGold(text = order.category)
                                                Text(
                                                    text = "${order.price.toInt()} ₸",
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = TaxiGold
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))
                                            RouteLegView(label = "Қайдан:", name = order.pickupAddress)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            RouteLegView(label = "Қайда:", name = order.destinationAddress)

                                            Spacer(modifier = Modifier.height(14.dp))
                                            Button(
                                                onClick = { viewModel.acceptOrder(order.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate),
                                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(text = "Тапсырысты алу", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- DRIVER NAVIGATING RECTO ACTIVE TRIP SCREEN ----------------
@Composable
fun ActiveTripDriverView(order: TaxiOrderEntity, viewModel: TaxiViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Белсенді сапар бағыты", color = TextGray, fontSize = 13.sp)
                Text(
                    text = when (order.status) {
                        "ACCEPTED" -> "Жолаушыны алу орнына барыңыз"
                        "ARRIVED" -> "Жолаушыны күтіп тұрсыз"
                        "STARTED" -> "Баратын жерге жолда"
                        else -> "Күтілуде"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TaxiGold,
                    textAlign = TextAlign.Center
                )
            }

            // Beautiful vector-based road simulation on high-contrast black board
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw black circuit road vector
                        val roadPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.8f)
                            quadraticTo(
                                size.width * 0.5f, size.height * 0.2f,
                                size.width * 0.8f, size.height * 0.3f
                            )
                        }
                        drawPath(
                            path = roadPath,
                            color = Color.DarkGray,
                            style = Stroke(width = 30f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                        )
                        drawPath(
                            path = roadPath,
                            color = TaxiGold,
                            style = Stroke(
                                width = 4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            )
                        )

                        // Draw start and end pin checkpoints
                        drawCircle(color = TaxiGold, radius = 10f, center = Offset(size.width * 0.2f, size.height * 0.8f))
                        drawCircle(color = Color.Green, radius = 10f, center = Offset(size.width * 0.8f, size.height * 0.3f))
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(SurfaceDark.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Text(text = "Жол қашықтығы:", fontSize = 10.sp, color = TextGray)
                        Text(text = "4.2 КМ (8 мин)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }

            // Passenger profile details card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Жолаушы мәліметтері:", fontSize = 12.sp, color = TaxiGold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Аты: ${order.passengerName}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text(text = "Телефон: ${order.passengerPhone}", fontSize = 13.sp, color = TextGray)

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(10.dp))

                    RouteLegView(label = "Бастау орны:", name = order.pickupAddress)
                    Spacer(modifier = Modifier.height(6.dp))
                    RouteLegView(label = "Баратын орны:", name = order.destinationAddress)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Тапсырыс бағасы:", fontSize = 12.sp, color = TextGray)
                        Text(text = "${order.price.toInt()} ₸", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TaxiGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stateful CTA progression button (Accepted -> Arrived -> Started -> Complete)
            Button(
                onClick = {
                    when (order.status) {
                        "ACCEPTED" -> viewModel.driverArrived(order.id)
                        "ARRIVED" -> viewModel.startTrip(order.id)
                        "STARTED" -> viewModel.completeTrip(order.id)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (order.status == "STARTED") SuccessGreen else TaxiGold,
                    contentColor = if (order.status == "STARTED") Color.White else DarkSlate
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (order.status) {
                        "ACCEPTED" -> "Клиент орнына жеттім (Келдім)"
                        "ARRIVED" -> "Клиент отырды (Сапарды бастау)"
                        "STARTED" -> "Жеттік (Сапарды аяқтау)"
                        else -> "Завершить"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// ---------------- SYSTEM ADMIN PANEL VIEW (SIMULATOR FOR REVIEWS) ----------------
@Composable
fun AdminPanelScreen(viewModel: TaxiViewModel) {
    val drivers by viewModel.allDrivers.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val isAdminAuth by viewModel.isAdminAuthenticated.collectAsStateWithLifecycle()
    val adminUsername by viewModel.adminUsernameInput.collectAsStateWithLifecycle()
    val adminPassword by viewModel.adminPasswordInput.collectAsStateWithLifecycle()
    val adminError by viewModel.adminAuthError.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Drivers, 1: Orders, 2: Statistics

    if (!isAdminAuth) {
        // SECURE ADMIN AUTHENTICATION SCREEN
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSlate),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(TaxiGold.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TaxiGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Орда Әкімшілік Панелі",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Text(
                        text = "Авторизациядан өту үшін логин мен құпия сөзді жазыңыз. Жүйені барынша қорғау белсенді.",
                        fontSize = 11.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    OutlinedTextField(
                        value = adminUsername,
                        onValueChange = { viewModel.adminUsernameInput.value = it },
                        label = { Text("Логин", color = TextGray) },
                        placeholder = { Text("admin", color = TextGray.copy(alpha = 0.5f)) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = TaxiGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedLabelColor = TaxiGold
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("admin_username_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { viewModel.adminPasswordInput.value = it },
                        label = { Text("Құпия сөз", color = TextGray) },
                        placeholder = { Text("орда2026...", color = TextGray.copy(alpha = 0.5f)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = TaxiGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                            focusedLabelColor = TaxiGold
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
                    )

                    if (adminError != null) {
                        Text(
                            text = adminError ?: "",
                            color = ErrorRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.submitAdminLogin() },
                        colors = ButtonDefaults.buttonColors(containerColor = TaxiGold, contentColor = DarkSlate),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_submit"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Кіру және Растау", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = { viewModel.navigateTo(Screen.Welcome) },
                        colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
                    ) {
                        Text("Басты бетке қайту", fontSize = 13.sp)
                    }
                }
            }
        }
    } else {
        // ADMIN AUTHORIZED LOGGED-IN CONSOLE!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSlate)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header of Admin
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(TaxiGold.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = TaxiGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Орда Әкімшілік басқару", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(text = "Жүйені толық бақылау режимі белсенді", fontSize = 11.sp, color = SuccessGreen)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.logoutAdmin() },
                        modifier = Modifier.background(ErrorRed.copy(alpha = 0.15f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Шығу", tint = ErrorRed)
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.08f), modifier = Modifier.padding(bottom = 12.dp))

                // Custom Modern Tabs representing drivers, orders, statistics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(SurfaceDark, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val tabs = listOf(
                        Triple(0, Icons.Default.DirectionsCar, "Жүргізушілер"),
                        Triple(1, Icons.Default.ListAlt, "Тапсырыстар"),
                        Triple(2, Icons.Default.Assessment, "Статистика")
                    )
                    tabs.forEach { (index, icon, label) ->
                        val selected = activeTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) TaxiGold else Color.Transparent)
                                .clickable { activeTab = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (selected) DarkSlate else TextGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) DarkSlate else TextWhite
                                )
                            }
                        }
                    }
                }

                // Tab Content
                when (activeTab) {
                    0 -> {
                        // DRIVERS MANAGEMENT TAB
                        Text(
                            text = "Тіркелген құжаттар өтінімі (${drivers.size}):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TaxiGold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (drivers.isEmpty()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Өтінімдер әлі жоқ. Драйвер тіркеп көріңіз.", color = TextGray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(drivers.size) { index ->
                                    val driver = drivers[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth().testTag("admin_driver_card"),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = driver.name,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextWhite
                                                )
                                                Text(
                                                    text = when(driver.status) {
                                                        "APPROVED" -> "Бекітілді ✅"
                                                        "REJECTED" -> "Қабылданбады ❌"
                                                        else -> "Күтуде ⏳"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = when (driver.status) {
                                                        "APPROVED" -> SuccessGreen
                                                        "REJECTED" -> ErrorRed
                                                        else -> TaxiGold
                                                    }
                                                )
                                            }

                                            Text(text = "Тел: ${driver.phone}", fontSize = 12.sp, color = TextGray)
                                            Text(text = "Көлік: ${driver.carBrandModel}", fontSize = 12.sp, color = TextGray)

                                            Spacer(modifier = Modifier.height(8.dp))
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(text = "Жүктелген құжаттар ақпараты:", fontSize = 10.sp, color = TaxiGold)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "📄 Права: ${driver.licenseUri}", fontSize = 10.sp, color = TextWhite)
                                                Text(text = "📸 Селфи: ${driver.selfieUri}", fontSize = 10.sp, color = TextWhite)
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "💳 Жеке куәлік: ${driver.idCardUri}", fontSize = 10.sp, color = TextWhite)
                                                Text(text = "🚘 Көлік фото: ${driver.carPhotoUri}", fontSize = 10.sp, color = TextWhite)
                                            }

                                            if (driver.status == "PENDING") {
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Button(
                                                        onClick = { viewModel.adminApproveDriver(driver.phone) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White),
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(text = "Рұқсат беру", fontSize = 11.sp)
                                                    }

                                                    Button(
                                                        onClick = { viewModel.adminRejectDriver(driver.phone) },
                                                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed, contentColor = Color.White),
                                                        shape = RoundedCornerShape(8.dp),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text(text = "Қабылдамау", fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // ORDERS MONITORING TAB
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Барлық тапсырыстар тізімі (${allOrders.size}):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiGold
                            )

                            if (allOrders.isNotEmpty()) {
                                TextButton(
                                    onClick = { viewModel.adminClearAllOrders() },
                                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                                ) {
                                    Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Тарихты тазарту", fontSize = 11.sp)
                                }
                            }
                        }

                        if (allOrders.isEmpty()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "Тапсырыстар тарихы бос.", color = TextGray)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(allOrders.size) { index ->
                                    val order = allOrders[index]
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "Тапсырыс #${order.id}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TaxiGold
                                                )
                                                Text(
                                                    text = order.status,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when(order.status) {
                                                        "COMPLETED" -> SuccessGreen
                                                        "CANCELLED" -> ErrorRed
                                                        "CREATED" -> TaxiGold
                                                        else -> Color.Cyan
                                                    }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "Жолаушы: ${order.passengerName} (${order.passengerPhone})", fontSize = 12.sp, color = TextWhite)
                                            Text(text = "📍 Қайдан: ${order.pickupAddress}", fontSize = 11.sp, color = TextGray)
                                            Text(text = "🏁 Қайда: ${order.destinationAddress}", fontSize = 11.sp, color = TextGray)
                                            Text(text = "💵 Бағасы: ${order.price.toInt()} ₸", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TaxiGold)

                                            if (order.driverName != null) {
                                                Text(
                                                    text = "🚗 Жүргізуші: ${order.driverName} (${order.driverPhone}) [${order.carBrandModel}]",
                                                    fontSize = 11.sp,
                                                    color = SuccessGreen,
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(
                                                    onClick = { viewModel.adminDeleteOrder(order.id) },
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(ErrorRed.copy(alpha = 0.12f), CircleShape)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Жою",
                                                        tint = ErrorRed,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // STATISTICS TAB
                        val completedRides = allOrders.filter { it.status == "COMPLETED" }
                        val activeRides = allOrders.filter { it.status != "COMPLETED" && it.status != "CANCELLED" }
                        val totalRevenue = completedRides.sumOf { it.price }
                        val onlineDrivers = drivers.filter { it.status == "APPROVED" }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Жүйенің қаржылық және сандық статистикасы:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TaxiGold
                            )

                            // Main Revenue card (Large)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                border = BorderStroke(1.dp, TaxiGold.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(imageVector = Icons.Default.Payments, contentDescription = null, tint = TaxiGold, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Жалпы айналым (Revenue)", fontSize = 12.sp, color = TextGray)
                                    Text(
                                        text = "${totalRevenue.toInt()} ₸",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TaxiGold
                                    )
                                    Text(text = "Барлық сәтті аяқталған сапарлардан", fontSize = 10.sp, color = TextGray)
                                }
                            }

                            // 2x2 Grid of stats
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Сәтті сапарлар", fontSize = 11.sp, color = TextGray)
                                        Text(text = completedRides.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = "Аяқталды", fontSize = 10.sp, color = SuccessGreen)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Белсенді сапарлар", fontSize = 11.sp, color = TextGray)
                                        Text(text = activeRides.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = "Қазір жолда", fontSize = 10.sp, color = TaxiGold)
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Жүргізушілер", fontSize = 11.sp, color = TextGray)
                                        Text(text = drivers.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = "Жалпы тіркелген", fontSize = 10.sp, color = TextGray)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(text = "Бекітілгендер", fontSize = 11.sp, color = TextGray)
                                        Text(text = onlineDrivers.size.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Text(text = "Жұмысқа дайын", fontSize = 10.sp, color = SuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.navigateTo(Screen.Welcome) },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = TextWhite),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Басты бетке оралу", color = TextWhite)
                }
            }
        }
    }
}

// ---------------- HELPER UI BLOCKS ----------------

// Beautiful Map illustration vector canvas (Like shown in the image illustration)
@Composable
fun TaxiInteractiveMap(
    pickupAddress: String,
    destAddress: String,
    durationMinutes: Int = 4,
    onMenuClick: () -> Unit,
    onMapTapPlace: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBEAE6)) // light warm aesthetic road background
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // Tap to instantly simulate selecting location of address
                    val names = listOf(
                        "Бәйтерек монументі, 1",
                        "Абай даңғылы, 105",
                        "Шымкент Сити орталығы",
                        "Алматы Арена, 3",
                        "Достық даңғылы, 48"
                    )
                    onMapTapPlace(names[Random.nextInt(names.size)])
                }
        ) {
            // Draw pastel street lines
            val streetColor = Color.White
            val gridColor = Color(0xFFDFDFDF)

            // Drawing background grid layout
            for (x in 0..size.width.toInt() step 120) {
                drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), strokeWidth = 1.5f)
            }
            for (y in 0..size.height.toInt() step 120) {
                drawLine(gridColor, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), strokeWidth = 1.5f)
            }

            // Drawing major roads
            // Vertical road
            drawRect(color = streetColor, topLeft = Offset(size.width * 0.35f, 0f), size = androidx.compose.ui.geometry.Size(80f, size.height))
            // Horizontal road
            drawRect(color = streetColor, topLeft = Offset(0f, size.height * 0.45f), size = androidx.compose.ui.geometry.Size(size.width, 80f))
            // Custom diagonal street
            drawLine(
                color = streetColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 60f
            )

            // Dynamic route drawing between markers if destination is set
            if (destAddress.isNotEmpty()) {
                val routePath = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width * 0.35f + 40f, size.height * 0.45f + 40f)
                    lineTo(size.width * 0.35f + 40f, size.height * 0.7f)
                    quadraticTo(
                        size.width * 0.35f + 40f, size.height * 0.85f,
                        size.width * 0.7f, size.height * 0.85f
                    )
                }
                drawPath(
                    path = routePath,
                    color = TaxiGold,
                    style = Stroke(width = 12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
        }

        // Draw Hamburger top left float button
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(46.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable { onMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Мәзір", tint = Color.Black)
        }

        // Map labels in Cyrillic to capture the exact realism from the photo
        LabelOnMap(text = "Сарыарқа даңғылы", modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp))
        LabelOnMap(text = "Достық даңғылы", modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 60.dp, end = 24.dp))
        LabelOnMap(text = "Орталық кітапхана", modifier = Modifier.align(Alignment.TopEnd).padding(top = 80.dp, end = 40.dp))

        // Center Location Pin containing dynamic durationMinutes exactly reflecting the photo
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-50).dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFC5A34)), // Orange-coral Pin accent from photo
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.border(1.5.dp, Color.White, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = durationMinutes.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "мин",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                // Little support pin tail
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFFFC5A34), CircleShape)
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }

        // Target Destination indicator flag if destination set (Green destination marker)
        if (destAddress.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(x = 60.dp, y = (-260).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Room,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Friendly instruction help tip
        Text(
            text = "Картаны басып баратын жерді таңдаңыз 👇",
            fontSize = 11.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun LabelOnMap(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray.copy(alpha = 0.7f),
        modifier = modifier
    )
}

@Composable
fun MockFilePickerRow(label: String, fileName: String, onPick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(12.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .clickable { onPick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = label, fontSize = 12.sp, color = TextGray)
            Text(text = fileName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (fileName.contains("жүктелді") || fileName != "куәлік.jpg" && fileName.contains(".png")) {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.CloudUpload
                },
                contentDescription = null,
                tint = if (fileName.contains("жүктелді") || fileName != "куәлік.jpg" && fileName.contains(".png")) {
                    SuccessGreen
                } else {
                    TaxiGold
                },
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun RouteLegView(label: String, name: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = TaxiGold,
            modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(text = label, fontSize = 10.sp, color = TextGray)
            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}

@Composable
fun TagGold(text: String) {
    Box(
        modifier = Modifier
            .background(TaxiGold.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .border(1.dp, TaxiGold.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = TaxiGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Dev Mode Quick Swapper component so testing roles (Passenger / Driver / Admin) is seamless!
@Composable
fun DevQuickSwapper(
    currentRole: String,
    onLogout: () -> Unit,
    onGoToAdmin: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceDark, CircleShape)
                .border(1.dp, TaxiGold, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.SwitchAccount,
                contentDescription = "Рөлдер",
                tint = TaxiGold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceDark)
        ) {
            Text(
                text = "Девелопер Панель",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TaxiGold,
                modifier = Modifier.padding(12.dp)
            )
            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            DropdownMenuItem(
                text = { Text("Админ панель") },
                onClick = {
                    onGoToAdmin()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null) }
            )

            HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

            DropdownMenuItem(
                text = { Text("Басты бетке (Шығу)") },
                onClick = {
                    onLogout()
                    expanded = false
                },
                leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = ErrorRed) }
            )
        }
    }
}
