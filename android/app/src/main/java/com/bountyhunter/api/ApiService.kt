package com.bountyhunter.api

import com.bountyhunter.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<User>
    
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("suggestions")
    suspend fun getSuggestions(@Header("Authorization") token: String): Response<List<Suggestion>>
    
    @POST("workouts")
    suspend fun logWorkout(
        @Header("Authorization") token: String,
        @Body request: LogWorkoutRequest
    ): Response<Workout>
    
    @GET("workouts")
    suspend fun getWorkoutHistory(@Header("Authorization") token: String): Response<List<Workout>>
    
    @GET("recurring")
    suspend fun getRecurringWorkouts(@Header("Authorization") token: String): Response<List<RecurringWorkout>>
    
    @POST("recurring")
    suspend fun createRecurringWorkout(
        @Header("Authorization") token: String,
        @Body request: CreateRecurringRequest
    ): Response<RecurringWorkout>
}
