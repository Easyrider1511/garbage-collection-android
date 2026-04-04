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
class ScheduleAdapterTest {

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
    fun `bind renders explicit address, scheduled date, and approved color`() {
        val adapter = ScheduleAdapter()
        val holder = adapter.onCreateViewHolder(parent, 0)

        holder.bind(
            FakeApiService.sampleSchedule(
                status = "APPROVED",
                scheduledDate = "2026-01-20T09:00:00",
                address = "Main Street"
            )
        )

        assertEquals("Old sofa pickup", holder.itemView.findViewById<TextView>(R.id.tvDescription).text)
        assertEquals("Furniture", holder.itemView.findViewById<TextView>(R.id.tvItemType).text)
        assertEquals("Approved", holder.itemView.findViewById<TextView>(R.id.tvStatus).text)
        assertEquals(0xFF1976D2.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
        assertEquals("Main Street", holder.itemView.findViewById<TextView>(R.id.tvAddress).text)
        assertEquals("2026-01-20T09:00:00", holder.itemView.findViewById<TextView>(R.id.tvScheduledDate).text)
    }

    @Test
    fun `bind falls back to coordinates and pending scheduling text`() {
        val adapter = ScheduleAdapter()
        val holder = adapter.onCreateViewHolder(parent, 0)

        holder.bind(
            FakeApiService.sampleSchedule(
                status = "CANCELLED",
                scheduledDate = null,
                address = null
            )
        )

        assertEquals("Cancelled", holder.itemView.findViewById<TextView>(R.id.tvStatus).text)
        assertEquals(0xFFD32F2F.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
        assertEquals("Lat: 39.8228, Lng: -7.4931", holder.itemView.findViewById<TextView>(R.id.tvAddress).text)
        assertEquals("Pending scheduling", holder.itemView.findViewById<TextView>(R.id.tvScheduledDate).text)
    }

    @Test
    fun `bind applies completed and default pending status colors`() {
        val adapter = ScheduleAdapter()
        val holder = adapter.onCreateViewHolder(parent, 0)

        holder.bind(FakeApiService.sampleSchedule(status = "COMPLETED"))
        assertEquals(0xFF388E3C.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)

        holder.bind(FakeApiService.sampleSchedule(status = "PENDING"))
        assertEquals(0xFFF57C00.toInt(), holder.itemView.findViewById<TextView>(R.id.tvStatus).currentTextColor)
    }

    @Test
    fun `diff callback compares item identity and content`() {
        val callback = ScheduleAdapter.DiffCallback()
        val first = FakeApiService.sampleSchedule(id = 1L)
        val sameIdDifferentContent = FakeApiService.sampleSchedule(id = 1L, status = "COMPLETED")
        val differentId = FakeApiService.sampleSchedule(id = 2L)

        assertTrue(callback.areItemsTheSame(first, sameIdDifferentContent))
        assertFalse(callback.areContentsTheSame(first, sameIdDifferentContent))
        assertFalse(callback.areItemsTheSame(first, differentId))
        assertTrue(callback.areContentsTheSame(first, FakeApiService.sampleSchedule(id = 1L)))
    }
}
