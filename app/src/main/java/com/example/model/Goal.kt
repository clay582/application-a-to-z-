package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadlineDate: String = "",
    val emojiIcon: String = "🎯",
    val createdAt: Long = System.currentTimeMillis()
)
