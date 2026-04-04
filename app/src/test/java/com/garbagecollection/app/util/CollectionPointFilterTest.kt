package com.garbagecollection.app.util

import com.garbagecollection.app.model.CollectionPointDTO
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionPointFilterTest {

    @Test
    fun `returns all points when all filter is selected`() {
        val points = listOf(
            collectionPoint(id = 1, collectionTypes = listOf("PAPER")),
            collectionPoint(id = 2, collectionTypes = listOf("GLASS", "PLASTIC"))
        )

        val result = CollectionPointFilter.filterByType(
            points,
            CollectionPointFilter.ALL_TYPES
        )

        assertEquals(points, result)
    }

    @Test
    fun `filters points by matching collection type`() {
        val points = listOf(
            collectionPoint(id = 1, collectionTypes = listOf("PAPER")),
            collectionPoint(id = 2, collectionTypes = listOf("GLASS", "PLASTIC")),
            collectionPoint(id = 3, collectionTypes = listOf("BATTERIES"))
        )

        val result = CollectionPointFilter.filterByType(points, "PLASTIC")

        assertEquals(listOf(points[1]), result)
    }

    @Test
    fun `collection type match is case insensitive`() {
        val points = listOf(
            collectionPoint(id = 1, collectionTypes = listOf("paper", "glass")),
            collectionPoint(id = 2, collectionTypes = listOf("organic"))
        )

        val result = CollectionPointFilter.filterByType(points, "GLASS")

        assertEquals(listOf(points[0]), result)
    }

    private fun collectionPoint(
        id: Long,
        collectionTypes: List<String>
    ) = CollectionPointDTO(
        id = id,
        name = "Point $id",
        description = null,
        latitude = 39.82,
        longitude = -7.49,
        address = null,
        collectionTypes = collectionTypes,
        status = "ACTIVE",
        createdByUserId = 1L
    )
}
