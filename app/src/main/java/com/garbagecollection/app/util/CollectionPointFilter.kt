package com.garbagecollection.app.util

import com.garbagecollection.app.model.CollectionPointDTO

object CollectionPointFilter {

    const val ALL_TYPES = "ALL"

    fun filterByType(
        collectionPoints: List<CollectionPointDTO>,
        selectedType: String
    ): List<CollectionPointDTO> {
        if (selectedType == ALL_TYPES) {
            return collectionPoints
        }

        return collectionPoints.filter { point ->
            point.collectionTypes.any { it.equals(selectedType, ignoreCase = true) }
        }
    }
}
