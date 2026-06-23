package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

class TaxiViewModel(application: Application, private val repository: TaxiRepository) : AndroidViewModel(application) {

    // Global navigation & auth state
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Welcome)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Form states
    // Passenger Auth
    val passNameInput = MutableStateFlow("")
    val passPhoneInput = MutableStateFlow("")
    val passPasswordInput = MutableStateFlow("")
    val passAuthError = MutableStateFlow<String?>(null)

    // Driver Onboarding
    val driverNameInput = MutableStateFlow("")
    val driverPhoneInput = MutableStateFlow("")
    val driverPasswordInput = MutableStateFlow("")
    val driverCarBrandModel = MutableStateFlow("")
    val driverLicenseText = MutableStateFlow("Жүргізуші куәлігі.webp")
    val driverIdText = MutableStateFlow("Жеке куәлік.webp")
    val driverCarPhotoText = MutableStateFlow("Машина_фото.webp")
    val driverSelfieText = MutableStateFlow("Селфи_куәлікпен.webp")
    val driverAuthError = MutableStateFlow<String?>(null)

    // Passenger Ride Booking state
    val pickupAddress = MutableStateFlow("Абай даңғылы, 45")
    val destinationAddress = MutableStateFlow("")
    val selectedTariff = MutableStateFlow("Стандарт") // Standard as requested (only category available)
    val ridePrice = MutableStateFlow(0.0)
    val predictedDurationMinutes = MutableStateFlow(4)
    val orderBookingError = MutableStateFlow<String?>(null)

    // Active passenger order being observed
    val activePassengerOrder = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "PASSENGER") {
            repository.observeActiveOrderForPassenger(user.phone)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Completed/all ride orders history of the passenger
    val passengerOrdersHistory = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "PASSENGER") {
            repository.observeOrdersForPassenger(user.phone)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Driver general states
    val curDriverProfile = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "DRIVER") {
            repository.observeDriverByPhone(user.phone)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // List of active available orders for all Online Drivers
    val availableOrders = repository.observeAvailableOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active driver order being observed
    val activeDriverOrder = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == "DRIVER") {
            repository.observeActiveOrderForDriver(user.phone)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // All driver registrations for Admin panel
    val allDrivers = repository.getAllDrivers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All taxi orders for real-time overview in Admin panel
    val allOrders = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Login Credentials & Authentication State
    val adminUsernameInput = MutableStateFlow("")
    val adminPasswordInput = MutableStateFlow("")
    val isAdminAuthenticated = MutableStateFlow(false)
    val adminAuthError = MutableStateFlow<String?>(null)

    init {
        restoreSession()
    }

    private fun saveSession(phone: String, role: String) {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("orda_taxi_prefs", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString("saved_phone", phone)
            .putString("saved_role", role)
            .apply()
    }

    private fun clearSession() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("orda_taxi_prefs", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .remove("saved_phone")
            .remove("saved_role")
            .apply()
    }

    private fun restoreSession() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("orda_taxi_prefs", android.content.Context.MODE_PRIVATE)
        val savedPhone = sharedPrefs.getString("saved_phone", null)
        val savedRole = sharedPrefs.getString("saved_role", null)
        if (!savedPhone.isNullOrBlank() && !savedRole.isNullOrBlank()) {
            viewModelScope.launch {
                val user = repository.getUserByPhone(savedPhone)
                if (user != null && user.role == savedRole) {
                    _currentUser.value = user
                    if (savedRole == "PASSENGER") {
                        seedPassengerHistoryIfNeeded(user.phone, user.name)
                        navigateTo(Screen.PassengerHome)
                    } else if (savedRole == "DRIVER") {
                        val driverProfile = repository.getDriverByPhone(user.phone)
                        if (driverProfile != null) {
                            if (driverProfile.status == "APPROVED") {
                                navigateTo(Screen.DriverHome)
                            } else {
                                navigateTo(Screen.DriverWaitingApproval)
                            }
                        } else {
                            navigateTo(Screen.DriverWaitingApproval)
                        }
                    }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    // Passenger Auth: Register or Login
    fun submitPassengerAuth() {
        val name = passNameInput.value.trim()
        val phone = passPhoneInput.value.trim()
        val pwd = passPasswordInput.value.trim()

        if (name.isEmpty() || phone.isEmpty() || pwd.isEmpty()) {
            passAuthError.value = "Барлық өрістерді толтырыңыз!"
            return
        }

        viewModelScope.launch {
            val existing = repository.getUserByPhone(phone)
            if (existing != null) {
                // If exists, perform simple login
                if (existing.password == pwd) {
                    _currentUser.value = existing
                    saveSession(phone, existing.role)
                    seedPassengerHistoryIfNeeded(existing.phone, existing.name)
                    passAuthError.value = null
                    navigateTo(Screen.PassengerHome)
                } else {
                    passAuthError.value = "Құпия сөз қате!"
                }
            } else {
                // Register new passenger
                val user = UserEntity(phone, name, pwd, "PASSENGER")
                repository.registerUser(user)
                _currentUser.value = user
                saveSession(phone, "PASSENGER")
                seedPassengerHistoryIfNeeded(user.phone, user.name)
                passAuthError.value = null
                navigateTo(Screen.PassengerHome)
            }
        }
    }

    // Driver Onboarding Auth & Register
    fun submitDriverOnboarding() {
        val name = driverNameInput.value.trim()
        val phone = driverPhoneInput.value.trim()
        val pwd = driverPasswordInput.value.trim()
        val car = driverCarBrandModel.value.trim()

        if (name.isEmpty() || phone.isEmpty() || pwd.isEmpty() || car.isEmpty()) {
            driverAuthError.value = "Барлық өрістерді толтырыңыз!"
            return
        }

        viewModelScope.launch {
            // Register general user credentials
            val user = UserEntity(phone, name, pwd, "DRIVER")
            repository.registerUser(user)

            // Register driver specifics
            val registration = DriverRegistrationEntity(
                phone = phone,
                name = name,
                carBrandModel = car,
                licenseUri = driverLicenseText.value,
                idCardUri = driverIdText.value,
                carPhotoUri = driverCarPhotoText.value,
                selfieUri = driverSelfieText.value,
                status = "PENDING", // initially PENDING
                isOnline = false
            )
            repository.registerDriver(registration)
            _currentUser.value = user
            saveSession(phone, "DRIVER")
            driverAuthError.value = null
            navigateTo(Screen.DriverWaitingApproval)
        }
    }

    // Toggle Online status for Driver
    fun toggleDriverOnline(isOnline: Boolean) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updateDriverOnline(user.phone, isOnline)
        }
    }

    fun updateOrder(order: TaxiOrderEntity) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    // Calculate dynamic price based on addresses
    fun calculatePriceOfRide() {
        val start = pickupAddress.value.trim()
        val end = destinationAddress.value.trim()
        if (start.isEmpty() || end.isEmpty()) {
            ridePrice.value = 0.0
            predictedDurationMinutes.value = 4
            return
        }
        // Basic calculation formula based on string lengths representing distance simulation
        val charLengthSum = start.length + end.length
        val simulatedDistance = (charLengthSum % 14) + 2.5 // Simulated distance 2.5km to 16.5km
        val baseFare = 400.0
        val perKm = 120.0
        // Rounded price in KZT (standard currency in Kazakhstan)
        val finalPrice = ((baseFare + (simulatedDistance * perKm)) / 10).toInt() * 10
        ridePrice.value = finalPrice.toDouble()
        // Simulated duration: length/hash based, minimum 5 minutes, approx 2.2 min per simulated km
        val mins = (simulatedDistance * 2.2).toInt().coerceAtLeast(5)
        predictedDurationMinutes.value = mins
    }

    // Create a new ride booking
    fun bookRide() {
        val user = _currentUser.value ?: return
        val start = pickupAddress.value.trim()
        val end = destinationAddress.value.trim()
        val price = ridePrice.value

        if (start.isEmpty() || end.isEmpty() || price <= 0.0) {
            orderBookingError.value = "Баратын жерді толтырыңыз және бағаны есептеңіз!"
            return
        }

        viewModelScope.launch {
            val order = TaxiOrderEntity(
                passengerPhone = user.phone,
                passengerName = user.name,
                pickupAddress = start,
                destinationAddress = end,
                price = price,
                status = "CREATED"
            )
            repository.createOrder(order)
            orderBookingError.value = null
            // Reset Booking fields
            destinationAddress.value = ""
            ridePrice.value = 0.0
        }
    }

    // Driver action: Accept Order
    fun acceptOrder(orderId: Long) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            val driverProf = repository.getDriverByPhone(user.phone)
            if (order != null && driverProf != null) {
                val updated = order.copy(
                    status = "ACCEPTED",
                    driverPhone = user.phone,
                    driverName = user.name,
                    carBrandModel = driverProf.carBrandModel
                )
                repository.updateOrder(updated)
            }
        }
    }

    // Driver action: Driver Arrived at pickup
    fun driverArrived(orderId: Long) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                repository.updateOrder(order.copy(status = "ARRIVED"))
            }
        }
    }

    // Driver action: Trip Started
    fun startTrip(orderId: Long) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                repository.updateOrder(order.copy(status = "STARTED"))
            }
        }
    }

    // Driver action: Complete trip
    fun completeTrip(orderId: Long) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                repository.updateOrder(order.copy(status = "COMPLETED"))
            }
        }
    }

    // Passenger rating / completion dismiss
    fun finishOrderRating(orderId: Long) {
        // Simple order final touch
        // Let's create dummy complete cycle so passenger returns to home
        viewModelScope.launch {
            // Nothing special, user can return to search
        }
    }

    // Admin Simulator action: Instantly approve a driver
    fun adminApproveDriver(phone: String) {
        viewModelScope.launch {
            repository.updateDriverStatus(phone, "APPROVED")
        }
    }

    // Admin Simulator action: Instantly reject a driver
    fun adminRejectDriver(phone: String) {
        viewModelScope.launch {
            repository.updateDriverStatus(phone, "REJECTED")
        }
    }

    // Secure login for real administrator
    fun submitAdminLogin() {
        val username = adminUsernameInput.value.trim()
        val password = adminPasswordInput.value.trim()
        if (username.isEmpty() || password.isEmpty()) {
            adminAuthError.value = "Логин мен құпия сөзді енгізіңіз!"
            return
        }
        // Secure protection comparison
        if (username == "admin" && password == "orda2026admin") {
            isAdminAuthenticated.value = true
            adminAuthError.value = null
            navigateTo(Screen.AdminPanel)
        } else {
            adminAuthError.value = "Логин немесе құпия сөз қате!"
        }
    }

    // Sign out from admin securely
    fun logoutAdmin() {
        isAdminAuthenticated.value = false
        adminUsernameInput.value = ""
        adminPasswordInput.value = ""
        adminAuthError.value = null
        navigateTo(Screen.Welcome)
    }

    // Delete single order from administrative DB
    fun adminDeleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrderById(orderId)
        }
    }

    // Clear all taxi orders from DB
    fun adminClearAllOrders() {
        viewModelScope.launch {
            repository.clearAllOrders()
        }
    }

    // Simulate Passenger login details instantly so testing is faster
    fun quickLoginAsPassenger(name: String, phone: String) {
        viewModelScope.launch {
            val pwd = "123"
            val user = UserEntity(phone, name, pwd, "PASSENGER")
            repository.registerUser(user)
            _currentUser.value = user
            saveSession(phone, "PASSENGER")
            navigateTo(Screen.PassengerHome)
        }
    }

    // Simulate Driver login/approval instantly
    fun quickLoginAsDriver(name: String, phone: String, car: String) {
        viewModelScope.launch {
            val pwd = "123"
            val user = UserEntity(phone, name, pwd, "DRIVER")
            repository.registerUser(user)

            val reg = DriverRegistrationEntity(
                phone = phone,
                name = name,
                carBrandModel = car,
                licenseUri = "куәлік.jpg",
                idCardUri = "жеке_куәлік.jpg",
                carPhotoUri = "машина.jpg",
                selfieUri = "селфи.jpg",
                status = "APPROVED",
                isOnline = true
            )
            repository.registerDriver(reg)
            _currentUser.value = user
            saveSession(phone, "DRIVER")
            navigateTo(Screen.DriverHome)
        }
    }

    fun seedPassengerHistoryIfNeeded(phone: String, name: String) {
        viewModelScope.launch {
            try {
                val existingList = repository.observeOrdersForPassenger(phone).first()
                if (existingList.isEmpty()) {
                    val order1 = TaxiOrderEntity(
                        passengerPhone = phone,
                        passengerName = name,
                        pickupAddress = "Бауыржан Момышұлы даңғылы, 14",
                        destinationAddress = "Хан Шатыр, ТРЦ",
                        category = "Стандарт",
                        price = 950.0,
                        status = "COMPLETED",
                        driverPhone = "87071112233",
                        driverName = "Дәурен",
                        carBrandModel = "Kia Rio, 047 ABS 01",
                        timestamp = System.currentTimeMillis() - 7200000 // 2 hours ago
                    )
                    val order2 = TaxiOrderEntity(
                        passengerPhone = phone,
                        passengerName = name,
                        pickupAddress = "Қабанбай батыр даңғылы, 53",
                        destinationAddress = "Нұр-Астана мешіті",
                        category = "Стандарт",
                        price = 1200.0,
                        status = "COMPLETED",
                        driverPhone = "87014445566",
                        driverName = "Әлібек",
                        carBrandModel = "Chevrolet Cobalt, 792 XYZ 02",
                        timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                    )
                    repository.createOrder(order1)
                    repository.createOrder(order2)
                }
            } catch (e: Exception) {
                // Safeguard
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        clearSession()
        navigateTo(Screen.Welcome)
    }
}

sealed class Screen {
    object Welcome : Screen()
    object PassengerAuth : Screen()
    object PassengerHome : Screen()
    object DriverOnboarding : Screen()
    object DriverWaitingApproval : Screen()
    object DriverHome : Screen()
    object AdminPanel : Screen()
}

class TaxiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaxiViewModel::class.java)) {
            val database = TaxiDatabase.getDatabase(application)
            val repository = TaxiRepository(database.taxiDao())
            return TaxiViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
