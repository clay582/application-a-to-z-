package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Yassine",
    val email: String = "yassine@example.com",
    val monthlySalary: Double = 8000.0,
    val salaryDay: Int = 25, // Day 1..31
    val fixedExpensesTotal: Double = 2500.0,
    val monthlySavingsTarget: Double = 1000.0,
    val language: String = "ar", // "ar" (Darija), "fr", "en"
    val currency: String = "DH",
    val darkModeEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val isOnboardingCompleted: Boolean = true
)
