package com.ashampoo.metadataproxy

import kotlinx.serialization.Serializable

@Serializable
data class MetadataUpdateRequest(
    val type: MetadataUpdateRequestType
)
