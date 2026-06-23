package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val phone: String,
    val name: String,
    val password: String,
    val role: String // "PASSENGER", "DRIVER"
)

@Entity(tableName = "driver_registrations")
data class DriverRegistrationEntity(
    @PrimaryKey val phone: String,
    val name: String,
    val carBrandModel: String,
    val licenseUri: String, // String representation or placeholder
    val idCardUri: String,
    val carPhotoUri: String,
    val selfieUri: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val isOnline: Boolean = false
)

@Entity(tableName = "taxi_orders")
data class TaxiOrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val passengerPhone: String,
    val passengerName: String,
    val pickupAddress: String,
    val destinationAddress: String,
    val category: String = "Стандарт", // "Стандарт" only, as requested
    val price: Double,
    val status: String, // CREATED, ACCEPTED, ARRIVED, STARTED, COMPLETED, CANCELLED
    val driverPhone: String? = null,
    val driverName: String? = null,
    val carBrandModel: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
