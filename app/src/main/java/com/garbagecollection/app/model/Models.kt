package com.garbagecollection.app.model

// Auth
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val username: String,
    val role: String,
    val userId: Long
)

data class UserDTO(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String,
    val role: String,
    val active: Boolean,
    val banned: Boolean,
    val phoneNumber: String?
)

// Collection Points
data class CollectionPointDTO(
    val id: Long,
    val name: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val collectionTypes: List<String>,
    val status: String,
    val createdByUserId: Long?
)

data class CreateCollectionPointRequest(
    val name: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val collectionTypes: List<String>
)

// Incidents
data class IncidentDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val type: String,
    val severity: String,
    val resolutionStatus: String,
    val collectionPointId: Long?,
    val reportedByUserId: Long,
    val photoUrl: String?,
    val reportCount: Int,
    val adminNotes: String?,
    val createdAt: String?,
    val resolvedAt: String?
)

data class CreateIncidentRequest(
    val title: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val type: String,
    val severity: String,
    val collectionPointId: Long? = null,
    val photoUrl: String? = null
)

// Pickup Schedules
data class PickupScheduleDTO(
    val id: Long,
    val description: String,
    val itemType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val requestedByUserId: Long,
    val requestedDate: String?,
    val scheduledDate: String?,
    val status: String,
    val adminNotes: String?,
    val createdAt: String?
)

data class CreatePickupRequest(
    val description: String,
    val itemType: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val requestedDate: String? = null
)
