package com.ashampoo.metadataproxy

import kotlinx.serialization.Serializable

@Serializable
data class FaceRegionArea(
    val xPos: Double,
    val yPos: Double,
    val width: Double,
    val height: Double
)
