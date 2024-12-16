/*
 * Copyright 2024 Ashampoo GmbH & Co. KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ashampoo.metadataproxy

import kotlinx.serialization.Serializable

@Serializable
data class MetadataUpdateRequest(

    /* Mandatory type field */
    val type: MetadataUpdateRequestType,

    /* MetadataUpdateRequestType.Orientation */
    val orientation: Int? = null,

    /* MetadataUpdateRequestType.TakenDate */
    val takenDate: Long? = null,

    /* MetadataUpdateRequestType.GpsCoordinates */
    val latitude: Double? = null,
    val longitude: Double? = null,

    /* MetadataUpdateRequestType.Flagged */
    val flagged: Boolean? = null,

    /* MetadataUpdateRequestType.Rating */
    val photoRating: Int? = null,

    /* MetadataUpdateRequestType.Keywords */
    val keywords: Set<String>? = null,

    /* MetadataUpdateRequestType.Faces */
    val faces: Map<String, FaceRegionArea>? = null,
    val widthPx: Int? = null,
    val heightPx: Int? = null,

    /* MetadataUpdateRequestType.Persons */
    val personsInImage: Set<String>? = null,

    /* MetadataUpdateRequestType.Albums */
    val albums: Set<String>? = null
)
