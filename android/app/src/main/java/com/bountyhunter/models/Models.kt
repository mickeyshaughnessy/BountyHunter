package com.bountyhunter.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("full_name") val fullName: String?,
    val location: String?,
    val age: Int?,
    val weight: Int?,
    @SerializedName("activity_types") val activityTypes: List<String>,
    @SerializedName("payment_info") val paymentInfo: String?
)

data class Suggestion(
    val id: Int,
    val title: String,
    val type: String,
    val duration: String
)

data class Workout(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val title: String,
    val type: String,
    val duration: String,
    val data: WorkoutData?,
    val timestamp: String
)

data class WorkoutData(
    val notes: String?
)

data class LogWorkoutRequest(
    val title: String,
    val type: String,
    val duration: String,
    val data: WorkoutData
)

data class RecurringWorkout(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val title: String,
    val schedule: String,
    @SerializedName("created_at") val createdAt: String
)

data class CreateRecurringRequest(
    val title: String,
    val schedule: String
)
