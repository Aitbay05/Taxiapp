package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        DriverRegistrationEntity::class,
        TaxiOrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TaxiDatabase : RoomDatabase() {
    abstract fun taxiDao(): TaxiDao

    companion object {
        @Volatile
        private var INSTANCE: TaxiDatabase? = null

        fun getDatabase(context: Context): TaxiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaxiDatabase::class.java,
                    "orda_taxi_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
