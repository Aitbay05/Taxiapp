package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxiDao {
    // User Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    // Driver Registration Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriverRegistration(driver: DriverRegistrationEntity)

    @Query("SELECT * FROM driver_registrations WHERE phone = :phone LIMIT 1")
    suspend fun getDriverByPhone(phone: String): DriverRegistrationEntity?

    @Query("SELECT * FROM driver_registrations WHERE phone = :phone LIMIT 1")
    fun observeDriverByPhone(phone: String): Flow<DriverRegistrationEntity?>

    @Query("SELECT * FROM driver_registrations")
    fun getAllDrivers(): Flow<List<DriverRegistrationEntity>>

    @Query("UPDATE driver_registrations SET status = :status WHERE phone = :phone")
    suspend fun updateDriverStatus(phone: String, status: String)

    @Query("UPDATE driver_registrations SET isOnline = :isOnline WHERE phone = :phone")
    suspend fun updateDriverOnline(phone: String, isOnline: Boolean)

    // Order Queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: TaxiOrderEntity): Long

    @Update
    suspend fun updateOrder(order: TaxiOrderEntity)

    @Query("SELECT * FROM taxi_orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): TaxiOrderEntity?

    @Query("SELECT * FROM taxi_orders WHERE id = :id LIMIT 1")
    fun observeOrderById(id: Long): Flow<TaxiOrderEntity?>

    @Query("SELECT * FROM taxi_orders WHERE passengerPhone = :phone ORDER BY timestamp DESC")
    fun observeOrdersForPassenger(phone: String): Flow<List<TaxiOrderEntity>>

    @Query("SELECT * FROM taxi_orders WHERE status = 'CREATED' ORDER BY timestamp DESC")
    fun observeAvailableOrders(): Flow<List<TaxiOrderEntity>>

    @Query("SELECT * FROM taxi_orders WHERE driverPhone = :phone AND status != 'COMPLETED' AND status != 'CANCELLED' LIMIT 1")
    fun observeActiveOrderForDriver(phone: String): Flow<TaxiOrderEntity?>

    @Query("SELECT * FROM taxi_orders WHERE passengerPhone = :phone AND status != 'COMPLETED' AND status != 'CANCELLED' LIMIT 1")
    fun observeActiveOrderForPassenger(phone: String): Flow<TaxiOrderEntity?>

    @Query("SELECT * FROM taxi_orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<TaxiOrderEntity>>

    @Query("DELETE FROM taxi_orders")
    suspend fun clearAllOrders()

    @Query("DELETE FROM taxi_orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Long)
}
