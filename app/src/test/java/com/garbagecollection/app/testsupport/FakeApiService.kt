package com.garbagecollection.app.testsupport

import com.garbagecollection.app.api.ApiService
import com.garbagecollection.app.model.AuthResponse
import com.garbagecollection.app.model.CollectionPointDTO
import com.garbagecollection.app.model.CreateCollectionPointRequest
import com.garbagecollection.app.model.CreateIncidentRequest
import com.garbagecollection.app.model.CreatePickupRequest
import com.garbagecollection.app.model.IncidentDTO
import com.garbagecollection.app.model.LoginRequest
import com.garbagecollection.app.model.PickupScheduleDTO
import com.garbagecollection.app.model.RegisterRequest
import com.garbagecollection.app.model.UserDTO
import retrofit2.Response

class FakeApiService : ApiService {

    var registerResponse: Response<AuthResponse> = Response.success(
        AuthResponse("register-token", "new-user", "USER", 10L)
    )
    var loginResponse: Response<AuthResponse> = Response.success(
        AuthResponse("login-token", "admin", "ADMIN", 1L)
    )
    var profileResponse: Response<UserDTO> = Response.success(
        UserDTO(
            id = 1L,
            username = "admin",
            email = "admin@garbagecollection.com",
            fullName = "System Administrator",
            role = "ADMIN",
            active = true,
            banned = false,
            phoneNumber = null
        )
    )
    var updateProfileResponse: Response<UserDTO> = profileResponse
    var allCollectionPointsResponse: Response<List<CollectionPointDTO>> = Response.success(
        listOf(sampleCollectionPoint())
    )
    var collectionPointResponse: Response<CollectionPointDTO> = Response.success(sampleCollectionPoint())
    var nearbyCollectionPointsResponse: Response<List<CollectionPointDTO>> = Response.success(
        listOf(sampleCollectionPoint())
    )
    var collectionPointsByTypeResponse: Response<List<CollectionPointDTO>> = Response.success(
        listOf(sampleCollectionPoint())
    )
    var createCollectionPointResponse: Response<CollectionPointDTO> = Response.success(
        sampleCollectionPoint()
    )
    var allIncidentsResponse: Response<List<IncidentDTO>> = Response.success(listOf(sampleIncident()))
    var incidentResponse: Response<IncidentDTO> = Response.success(sampleIncident())
    var myIncidentsResponse: Response<List<IncidentDTO>> = Response.success(listOf(sampleIncident()))
    var createIncidentResponse: Response<IncidentDTO> = Response.success(sampleIncident())
    var reportSameIncidentResponse: Response<IncidentDTO> = Response.success(
        sampleIncident(reportCount = 3)
    )
    var updateIncidentStatusResponse: Response<IncidentDTO> = Response.success(
        sampleIncident(resolutionStatus = "RESOLVED")
    )
    var allSchedulesResponse: Response<List<PickupScheduleDTO>> = Response.success(
        listOf(sampleSchedule())
    )
    var mySchedulesResponse: Response<List<PickupScheduleDTO>> = Response.success(
        listOf(sampleSchedule())
    )
    var createPickupScheduleResponse: Response<PickupScheduleDTO> = Response.success(
        sampleSchedule()
    )
    var updateScheduleStatusResponse: Response<PickupScheduleDTO> = Response.success(
        sampleSchedule(status = "SCHEDULED")
    )

    var lastRegisterRequest: RegisterRequest? = null
    var lastLoginRequest: LoginRequest? = null
    var lastProfileUpdateRequest: RegisterRequest? = null
    var lastCreateCollectionPointRequest: CreateCollectionPointRequest? = null
    var lastCreateIncidentRequest: CreateIncidentRequest? = null
    var lastCreatePickupRequest: CreatePickupRequest? = null
    var lastRequestedUserId: Long? = null
    var lastReportIncidentId: Long? = null
    var lastUpdatedIncidentId: Long? = null
    var lastUpdatedIncidentStatus: String? = null
    var lastUpdatedIncidentNotes: String? = null
    var lastUpdatedScheduleId: Long? = null
    var lastUpdatedScheduleStatus: String? = null
    var lastUpdatedScheduleDate: String? = null
    var lastUpdatedScheduleNotes: String? = null

    override suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        lastRegisterRequest = request
        return registerResponse
    }

    override suspend fun login(request: LoginRequest): Response<AuthResponse> {
        lastLoginRequest = request
        return loginResponse
    }

    override suspend fun getProfile(): Response<UserDTO> = profileResponse

    override suspend fun updateProfile(request: RegisterRequest): Response<UserDTO> {
        lastProfileUpdateRequest = request
        return updateProfileResponse
    }

    override suspend fun getAllCollectionPoints(): Response<List<CollectionPointDTO>> =
        allCollectionPointsResponse

    override suspend fun getCollectionPoint(id: Long): Response<CollectionPointDTO> =
        collectionPointResponse

    override suspend fun getNearbyCollectionPoints(
        lat: Double,
        lng: Double,
        radius: Double
    ): Response<List<CollectionPointDTO>> = nearbyCollectionPointsResponse

    override suspend fun getCollectionPointsByType(
        type: String
    ): Response<List<CollectionPointDTO>> = collectionPointsByTypeResponse

    override suspend fun createCollectionPoint(
        request: CreateCollectionPointRequest
    ): Response<CollectionPointDTO> {
        lastCreateCollectionPointRequest = request
        return createCollectionPointResponse
    }

    override suspend fun getAllIncidents(): Response<List<IncidentDTO>> = allIncidentsResponse

    override suspend fun getIncident(id: Long): Response<IncidentDTO> = incidentResponse

    override suspend fun getMyIncidents(userId: Long): Response<List<IncidentDTO>> {
        lastRequestedUserId = userId
        return myIncidentsResponse
    }

    override suspend fun createIncident(request: CreateIncidentRequest): Response<IncidentDTO> {
        lastCreateIncidentRequest = request
        return createIncidentResponse
    }

    override suspend fun reportSameIncident(id: Long): Response<IncidentDTO> {
        lastReportIncidentId = id
        return reportSameIncidentResponse
    }

    override suspend fun updateIncidentStatus(
        id: Long,
        status: String,
        adminNotes: String?
    ): Response<IncidentDTO> {
        lastUpdatedIncidentId = id
        lastUpdatedIncidentStatus = status
        lastUpdatedIncidentNotes = adminNotes
        return updateIncidentStatusResponse
    }

    override suspend fun getAllSchedules(): Response<List<PickupScheduleDTO>> = allSchedulesResponse

    override suspend fun getMySchedules(userId: Long): Response<List<PickupScheduleDTO>> {
        lastRequestedUserId = userId
        return mySchedulesResponse
    }

    override suspend fun createPickupSchedule(
        request: CreatePickupRequest
    ): Response<PickupScheduleDTO> {
        lastCreatePickupRequest = request
        return createPickupScheduleResponse
    }

    override suspend fun updateScheduleStatus(
        id: Long,
        status: String,
        scheduledDate: String?,
        adminNotes: String?
    ): Response<PickupScheduleDTO> {
        lastUpdatedScheduleId = id
        lastUpdatedScheduleStatus = status
        lastUpdatedScheduleDate = scheduledDate
        lastUpdatedScheduleNotes = adminNotes
        return updateScheduleStatusResponse
    }

    companion object {
        fun sampleCollectionPoint(
            id: Long = 1L,
            collectionTypes: List<String> = listOf("PAPER", "GLASS"),
            status: String = "ACTIVE"
        ) = CollectionPointDTO(
            id = id,
            name = "Central Eco Point",
            description = "Downtown collection point",
            latitude = 39.8228,
            longitude = -7.4931,
            address = "Main Street",
            collectionTypes = collectionTypes,
            status = status,
            createdByUserId = 1L
        )

        fun sampleIncident(
            id: Long = 1L,
            severity: String = "HIGH",
            resolutionStatus: String = "OPEN",
            photoUrl: String? = null,
            reportCount: Int = 2
        ) = IncidentDTO(
            id = id,
            title = "Overflowing container",
            description = "Paper container is full",
            latitude = 39.8228,
            longitude = -7.4931,
            address = "Main Street",
            type = "OVERFLOW",
            severity = severity,
            resolutionStatus = resolutionStatus,
            collectionPointId = 1L,
            reportedByUserId = 1L,
            photoUrl = photoUrl,
            reportCount = reportCount,
            adminNotes = "Review scheduled",
            createdAt = "2026-01-15T10:00:00",
            resolvedAt = null
        )

        fun sampleSchedule(
            id: Long = 1L,
            status: String = "PENDING",
            scheduledDate: String? = "2026-01-20T09:00:00",
            address: String? = "Main Street"
        ) = PickupScheduleDTO(
            id = id,
            description = "Old sofa pickup",
            itemType = "FURNITURE",
            latitude = 39.8228,
            longitude = -7.4931,
            address = address,
            requestedByUserId = 1L,
            requestedDate = "2026-01-18T10:00:00",
            scheduledDate = scheduledDate,
            status = status,
            adminNotes = "Assigned to route 2",
            createdAt = "2026-01-18T10:00:00"
        )
    }
}
