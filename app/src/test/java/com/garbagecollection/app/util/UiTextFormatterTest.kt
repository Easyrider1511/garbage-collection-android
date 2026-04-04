package com.garbagecollection.app.util

import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UiTextFormatterTest {

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        AppLanguageManager.applyLanguage(TestFixtures.appContext(), AppLanguageManager.LANGUAGE_EN)
    }

    @Test
    fun `formats known incident, severity, status, collection and role codes`() {
        val context = TestFixtures.appContext()

        assertEquals(
            context.getString(R.string.incident_type_missing_container),
            UiTextFormatter.incidentType(context, "MISSING_CONTAINER")
        )
        assertEquals(
            context.getString(R.string.incident_type_vandalized_container),
            UiTextFormatter.incidentType(context, "VANDALIZED_CONTAINER")
        )
        assertEquals(
            context.getString(R.string.incident_type_hazardous_spill),
            UiTextFormatter.incidentType(context, "HAZARDOUS_SPILL")
        )
        assertEquals(
            context.getString(R.string.incident_type_overflow),
            UiTextFormatter.incidentType(context, "overflow")
        )
        assertEquals(
            context.getString(R.string.incident_type_illegal_dumping),
            UiTextFormatter.incidentType(context, "ILLEGAL_DUMPING")
        )
        assertEquals(
            context.getString(R.string.incident_type_damaged_equipment),
            UiTextFormatter.incidentType(context, "DAMAGED_EQUIPMENT")
        )
        assertEquals(
            context.getString(R.string.item_other),
            UiTextFormatter.incidentType(context, "OTHER")
        )
        assertEquals(
            context.getString(R.string.severity_low),
            UiTextFormatter.severity(context, "LOW")
        )
        assertEquals(
            context.getString(R.string.severity_medium),
            UiTextFormatter.severity(context, "MEDIUM")
        )
        assertEquals(
            context.getString(R.string.severity_high),
            UiTextFormatter.severity(context, "HIGH")
        )
        assertEquals(
            context.getString(R.string.severity_critical),
            UiTextFormatter.severity(context, "critical")
        )
        assertEquals(
            context.getString(R.string.status_open),
            UiTextFormatter.incidentStatus(context, "OPEN")
        )
        assertEquals(
            context.getString(R.string.status_pending),
            UiTextFormatter.incidentStatus(context, "PENDING")
        )
        assertEquals(
            context.getString(R.string.status_in_progress),
            UiTextFormatter.incidentStatus(context, "in-progress")
        )
        assertEquals(
            context.getString(R.string.status_resolved),
            UiTextFormatter.incidentStatus(context, "RESOLVED")
        )
        assertEquals(
            context.getString(R.string.status_closed),
            UiTextFormatter.incidentStatus(context, "CLOSED")
        )
        assertEquals(
            context.getString(R.string.status_rejected),
            UiTextFormatter.incidentStatus(context, "REJECTED")
        )
        assertEquals(
            context.getString(R.string.status_pending),
            UiTextFormatter.scheduleStatus(context, "PENDING")
        )
        assertEquals(
            context.getString(R.string.status_approved),
            UiTextFormatter.scheduleStatus(context, "APPROVED")
        )
        assertEquals(
            context.getString(R.string.status_scheduled),
            UiTextFormatter.scheduleStatus(context, "scheduled")
        )
        assertEquals(
            context.getString(R.string.status_completed),
            UiTextFormatter.scheduleStatus(context, "COMPLETED")
        )
        assertEquals(
            context.getString(R.string.status_cancelled),
            UiTextFormatter.scheduleStatus(context, "CANCELLED")
        )
        assertEquals(
            context.getString(R.string.status_rejected),
            UiTextFormatter.scheduleStatus(context, "REJECTED")
        )
        assertEquals(
            context.getString(R.string.pickup_item_furniture),
            UiTextFormatter.pickupItemType(context, "FURNITURE")
        )
        assertEquals(
            context.getString(R.string.pickup_item_electronics),
            UiTextFormatter.pickupItemType(context, "ELECTRONICS")
        )
        assertEquals(
            context.getString(R.string.pickup_item_appliances),
            UiTextFormatter.pickupItemType(context, "APPLIANCES")
        )
        assertEquals(
            context.getString(R.string.pickup_item_mattress),
            UiTextFormatter.pickupItemType(context, "MATTRESS")
        )
        assertEquals(
            context.getString(R.string.pickup_item_construction_debris),
            UiTextFormatter.pickupItemType(context, "construction_debris")
        )
        assertEquals(
            context.getString(R.string.item_other),
            UiTextFormatter.pickupItemType(context, "OTHER")
        )
        assertEquals(
            context.getString(R.string.collection_type_batteries),
            UiTextFormatter.collectionType(context, "BATTERIES")
        )
        assertEquals(
            context.getString(R.string.collection_type_electronics),
            UiTextFormatter.collectionType(context, "ELECTRONICS")
        )
        assertEquals(
            context.getString(R.string.collection_type_bulky_items),
            UiTextFormatter.collectionType(context, "BULKY_ITEMS")
        )
        assertEquals(
            context.getString(R.string.collection_type_paper),
            UiTextFormatter.collectionType(context, "PAPER")
        )
        assertEquals(
            context.getString(R.string.collection_type_glass),
            UiTextFormatter.collectionType(context, "GLASS")
        )
        assertEquals(
            context.getString(R.string.collection_type_plastic),
            UiTextFormatter.collectionType(context, "PLASTIC")
        )
        assertEquals(
            context.getString(R.string.collection_type_organic),
            UiTextFormatter.collectionType(context, "ORGANIC")
        )
        assertEquals(
            context.getString(R.string.collection_type_mixed_waste),
            UiTextFormatter.collectionType(context, "mixed-waste")
        )
        assertEquals(
            context.getString(R.string.collection_type_metal),
            UiTextFormatter.collectionType(context, "METAL")
        )
        assertEquals(
            context.getString(R.string.collection_type_clothes),
            UiTextFormatter.collectionType(context, "CLOTHES")
        )
        assertEquals(
            context.getString(R.string.item_other),
            UiTextFormatter.collectionType(context, "OTHER")
        )
        assertEquals(
            context.getString(R.string.status_active),
            UiTextFormatter.collectionPointStatus(context, "ACTIVE")
        )
        assertEquals(
            context.getString(R.string.status_inactive),
            UiTextFormatter.collectionPointStatus(context, "INACTIVE")
        )
        assertEquals(
            context.getString(R.string.collection_point_status_full),
            UiTextFormatter.collectionPointStatus(context, "FULL")
        )
        assertEquals(
            context.getString(R.string.collection_point_status_maintenance),
            UiTextFormatter.collectionPointStatus(context, "maintenance")
        )
        assertEquals(
            context.getString(R.string.role_admin),
            UiTextFormatter.userRole(context, "admin")
        )
        assertEquals(
            context.getString(R.string.role_user),
            UiTextFormatter.userRole(context, "USER")
        )
    }

    @Test
    fun `humanizes unknown values`() {
        val context = TestFixtures.appContext()

        assertEquals("Custom case", UiTextFormatter.incidentType(context, "CUSTOM_CASE"))
        assertEquals("Unknown level", UiTextFormatter.severity(context, "unknown_level"))
        assertEquals("Needs review", UiTextFormatter.incidentStatus(context, "needs_review"))
        assertEquals("Awaiting operator", UiTextFormatter.scheduleStatus(context, "awaiting_operator"))
        assertEquals("Bulk wood", UiTextFormatter.pickupItemType(context, "bulk_wood"))
        assertEquals("Blue glass", UiTextFormatter.collectionType(context, "blue_glass"))
        assertEquals("Paused", UiTextFormatter.collectionPointStatus(context, "paused"))
        assertEquals("Supervisor", UiTextFormatter.userRole(context, "supervisor"))
    }
}
