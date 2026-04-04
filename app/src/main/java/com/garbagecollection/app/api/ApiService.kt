package com.garbagecollection.app.api

import com.garbagecollection.app.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // User profile
    @GET("api/users/me")
    suspend fun getProfile(): Response<UserDTO>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: RegisterRequest): Response<UserDTO>

    // Collection Points
    @GET("api/collection-points")
    suspend fun getAllCollectionPoints(): Response<List<CollectionPointDTO>>

    @GET("api/collection-points/{id}")
    suspend fun getCollectionPoint(@Path("id") id: Long): Response<CollectionPointDTO>

    @GET("api/collection-points/nearby")
    suspend fun getNearbyCollectionPoints(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double = 5.0
    ): Response<List<CollectionPointDTO>>

    @GET("api/collection-points/type/{type}")
    suspend fun getCollectionPointsByType(@Path("type") type: String): Response<List<CollectionPointDTO>>

    @POST("api/collection-points")
    suspend fun createCollectionPoint(@Body request: CreateCollectionPointRequest): Response<CollectionPointDTO>

    // Incidents
    @GET("api/incidents")
    suspend fun getAllIncidents(): Response<List<IncidentDTO>>

    @GET("api/incidents/{id}")
    suspend fun getIncident(@Path("id") id: Long): Response<IncidentDTO>

    @GET("api/incidents/user/{userId}")
    suspend fun getMyIncidents(@Path("userId") userId: Long): Response<List<IncidentDTO>>

    @POST("api/incidents")
    suspend fun createIncident(@Body request: CreateIncidentRequest): Response<IncidentDTO>

    @PATCH("api/incidents/{id}/report")
    suspend fun reportSameIncident(@Path("id") id: Long): Response<IncidentDTO>

    @PATCH("api/incidents/{id}/status")
    suspend fun updateIncidentStatus(
        @Path("id") id: Long,
        @Query("status") status: String,
        @Query("adminNotes") adminNotes: String? = null
    ): Response<IncidentDTO>

    // Pickup Schedules
    @GET("api/schedules")
    suspend fun getAllSchedules(): Response<List<PickupScheduleDTO>>

    @GET("api/schedules/user/{userId}")
    suspend fun getMySchedules(@Path("userId") userId: Long): Response<List<PickupScheduleDTO>>

    @POST("api/schedules")
    suspend fun createPickupSchedule(@Body request: CreatePickupRequest): Response<PickupScheduleDTO>

    @PATCH("api/schedules/{id}/status")
    suspend fun updateScheduleStatus(
        @Path("id") id: Long,
        @Query("status") status: String,
        @Query("scheduledDate") scheduledDate: String? = null,
        @Query("adminNotes") adminNotes: String? = null
    ): Response<PickupScheduleDTO>
}
