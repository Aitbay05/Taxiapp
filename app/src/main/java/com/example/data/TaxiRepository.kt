package com.example.data

import kotlinx.coroutines.flow.Flow

class TaxiRepository(private val taxiDao: TaxiDao) {

    // User Session actions
    suspend fun registerUser(user: UserEntity) {
        taxiDao.insertUser(user)
    }

    suspend fun getUserByPhone(phone: String): UserEntity? {
        return taxiDao.getUserByPhone(phone)
    }

    // Driver Registration actions
    suspend fun registerDriver(driver: DriverRegistrationEntity) {
        taxiDao.insertDriverRegistration(driver)
    }

    suspend fun getDriverByPhone(phone: String): DriverRegistrationEntity? {
        return taxiDao.getDriverByPhone(phone)
    }

    fun observeDriverByPhone(phone: String): Flow<DriverRegistrationEntity?> {
        return taxiDao.observeDriverByPhone(phone)
    }

    fun getAllDrivers(): Flow<List<DriverRegistrationEntity>> {
        return taxiDao.getAllDrivers()
    }

    suspend fun updateDriverStatus(phone: String, status: String) {
        taxiDao.updateDriverStatus(phone, status)
    }

    suspend fun updateDriverOnline(phone: String, isOnline: Boolean) {
        taxiDao.updateDriverOnline(phone, isOnline)
    }

    // Taxi Order actions
    suspend fun createOrder(order: TaxiOrderEntity): Long {
        return taxiDao.insertOrder(order)
    }

    suspend fun updateOrder(order: TaxiOrderEntity) {
        taxiDao.updateOrder(order)
    }

    suspend fun getOrderById(id: Long): TaxiOrderEntity? {
        return taxiDao.getOrderById(id)
    }

    fun observeOrderById(id: Long): Flow<TaxiOrderEntity?> {
        return taxiDao.observeOrderById(id)
    }

    fun observeOrdersForPassenger(phone: String): Flow<List<TaxiOrderEntity>> {
        return taxiDao.observeOrdersForPassenger(phone)
    }

    fun observeAvailableOrders(): Flow<List<TaxiOrderEntity>> {
        return taxiDao.observeAvailableOrders()
    }

    fun observeActiveOrderForDriver(phone: String): Flow<TaxiOrderEntity?> {
        return taxiDao.observeActiveOrderForDriver(phone)
    }

    fun observeActiveOrderForPassenger(phone: String): Flow<TaxiOrderEntity?> {
        return taxiDao.observeActiveOrderForPassenger(phone)
    }

    fun getAllOrders(): Flow<List<TaxiOrderEntity>> {
        return taxiDao.getAllOrders()
    }

    suspend fun clearAllOrders() {
        taxiDao.clearAllOrders()
    }

    suspend fun deleteOrderById(orderId: Long) {
        taxiDao.deleteOrderById(orderId)
    }
}
