package com.garbagecollection.app.adapter

import android.view.ContextThemeWrapper
import android.widget.FrameLayout
import android.widget.TextView
import com.garbagecollection.app.R
import com.garbagecollection.app.testsupport.FakeApiService
import com.garbagecollection.app.testsupport.TestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class IncidentAdapterTest {

    private lateinit var parent: FrameLayout

    @Before
    fun setUp() {
        TestFixtures.clearAppState()
        parent = FrameLayout(
            ContextThemeWrapper(
                TestFixtures.appContext(),
                R.style.Theme_GarbageCollection
            )
        )
    }

    @Test
    fun `bind renders incident data, applies colors, and dispatches click`() {
        var clickedIncidentId: Long? = null
        val adapter = IncidentAdapter { clickedIncidentId = it.id }
        val holder = adapter.onCreateViewHolder(parent, 0)
        val incident = FakeApiService.sampleIncident(
            id = 7L,
            severity = "CRITICAL",
            resolutionStatus = "RESOLVED",
            reportCount = 1
        )

        holder.bind(incident)
        holder.itemView.performClick()

        assertEquals("Overflowing container", holder.itemView.findViewById<TextView>(R.id.tvTitle).text)
        assertEquals("Overflow", holder.itemView.findViewById<TextView>(R.id.tvType).text)
        assertEquals("Critical", holder.itemView.findViewById<TextView>(R.id.tvSeverity).text)
        assertEquals(0xFFD32F2F.toInt(), holder.itemView.findViewById<TextView>(R.id.tvSeverity).currentTextColor)
        assertEquals("Resolved", holder.itemView.findViewById<TextView>(R.id.tvStatus).text)
        assertEquals(0xFF388E3C.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
        assertEquals("1 report", holder.itemView.findViewById<TextView>(R.id.tvReports).text)
        assertEquals(7L, clickedIncidentId)
    }

    @Test
    fun `bind applies medium and default status colors`() {
        val adapter = IncidentAdapter {}
        val holder = adapter.onCreateViewHolder(parent, 0)

        holder.bind(
            FakeApiService.sampleIncident(
                severity = "MEDIUM",
                resolutionStatus = "IN_PROGRESS",
                reportCount = 4
            )
        )

        assertEquals(0xFFFFA000.toInt(), holder.itemView.findViewById<TextView>(R.id.tvSeverity).currentTextColor)
        assertEquals(0xFF1976D2.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
        assertEquals("4 reports", holder.itemView.findViewById<TextView>(R.id.tvReports).text)

        holder.bind(FakeApiService.sampleIncident(severity = "LOW", resolutionStatus = "OPEN"))

        assertEquals(0xFF388E3C.toInt(), holder.itemView.findViewById<TextView>(R.id.tvSeverity).currentTextColor)
        assertEquals(0xFFF57C00.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
    }

    @Test
    fun `diff callback compares item identity and content`() {
        val callback = IncidentAdapter.DiffCallback()
        val first = FakeApiService.sampleIncident(id = 1L)
        val sameIdDifferentContent = FakeApiService.sampleIncident(id = 1L, severity = "LOW")
        val differentId = FakeApiService.sampleIncident(id = 2L)

        assertTrue(callback.areItemsTheSame(first, sameIdDifferentContent))
        assertFalse(callback.areContentsTheSame(first, sameIdDifferentContent))
        assertFalse(callback.areItemsTheSame(first, differentId))
        assertTrue(callback.areContentsTheSame(first, FakeApiService.sampleIncident(id = 1L)))
    }
}
