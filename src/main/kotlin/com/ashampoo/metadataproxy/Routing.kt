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

import com.ashampoo.kim.Kim
import com.ashampoo.kim.model.GpsCoordinates
import com.ashampoo.kim.model.MetadataUpdate
import com.ashampoo.kim.model.PhotoRating
import com.ashampoo.kim.model.TiffOrientation
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val SERVER_BANNER = "Ashampoo Metadata Proxy Service"
private const val AUTHORIZATION_HEADER = "Authorization"

private val httpClient = HttpClient()

fun Application.configureRouting() {
    routing {

        get("/") {

            call.respondText(SERVER_BANNER)
        }

        post("/") {

            val remoteUrl = call.request.header("RemoteUrl")

            if (remoteUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Header 'RemoteUrl' is missing.")
                return@post
            }

            val updateRequest = call.receive<MetadataUpdateRequest>()

            println("Received request: $updateRequest")

            val authToken = call.request.header(AUTHORIZATION_HEADER)

            val getResponse = httpClient.get(remoteUrl) {

                /* If set, pass the auth token on to the remote service */
                if (authToken != null)
                    header(AUTHORIZATION_HEADER, authToken)
            }

            /* If the remote URL requires authorization we forward it as is. */
            if (getResponse.status == HttpStatusCode.Unauthorized) {
                call.respond(getResponse.status, getResponse.bodyAsText())
                return@post
            }

            if (!getResponse.status.isSuccess()) {

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "Call to remove URL responded with ${getResponse.status}"
                )

                return@post
            }

            val remoteBytes = getResponse.bodyAsBytes()

            val updatedBytes = try {

                updateBytes(updateRequest, remoteBytes)

            } catch (ex: Exception) {

                call.respond(HttpStatusCode.BadRequest, ex.message ?: "Invalid data.")
                return@post
            }

            val putResponse = httpClient.put(remoteUrl) {

                /* If set, pass the auth token on to the remote service */
                if (authToken != null)
                    header(AUTHORIZATION_HEADER, authToken)

                contentType(ContentType.Application.OctetStream)
                setBody(updatedBytes)
            }

            /*
             * If the PUT failed, return the error code to the client.
             */
            if (!putResponse.status.isSuccess()) {
                call.respond(getResponse.status, getResponse.bodyAsText())
                return@post
            }

            call.respond(HttpStatusCode.OK, "Metadata updated.")
        }
    }
}

private fun updateBytes(
    updateRequest: MetadataUpdateRequest,
    remoteBytes: ByteArray
): ByteArray = when (updateRequest.type) {

    MetadataUpdateRequestType.Orientation -> {

        val orientation: Int = updateRequest.orientation
            ?: error("Field 'orientation' must not be NULL.")

        val tiffOrientation = TiffOrientation.of(orientation)
            ?: error("Field 'orientation' has illegal value: $orientation")

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.Orientation(
                tiffOrientation = tiffOrientation
            )
        )
    }

    MetadataUpdateRequestType.TakenDate -> {

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.TakenDate(
                takenDate = updateRequest.takenDate
            )
        )
    }

    MetadataUpdateRequestType.GpsCoordinates -> {

        val gpsCoordinates = if (updateRequest.latitude != null && updateRequest.longitude != null)
            GpsCoordinates(
                latitude = updateRequest.latitude,
                longitude = updateRequest.longitude
            )
        else
            null

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.GpsCoordinates(
                gpsCoordinates = gpsCoordinates
            )
        )
    }

    MetadataUpdateRequestType.Flagged -> {

        val flagged: Boolean = updateRequest.flagged
            ?: error("Field 'flagged' must not be NULL.")

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.Flagged(
                flagged = flagged
            )
        )
    }

    MetadataUpdateRequestType.Rating -> {

        val rating: Int = updateRequest.photoRating
            ?: error("Field 'photoRating' must not be NULL.")

        val photoRating = PhotoRating.of(rating)
            ?: error("Field 'photoRating' has illegal value: $rating")

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.Rating(
                photoRating = photoRating
            )
        )
    }

    MetadataUpdateRequestType.Keywords -> {

        val keywords: Set<String> = updateRequest.keywords
            ?: error("Field 'keywords' must not be NULL.")

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.Keywords(
                keywords = keywords
            )
        )
    }

    // TODO Faces

    MetadataUpdateRequestType.Persons -> {

        val personsInImage: Set<String> = updateRequest.personsInImage
            ?: error("Field 'personsInImage' must not be NULL.")

        Kim.update(
            bytes = remoteBytes,
            update = MetadataUpdate.Persons(
                personsInImage = personsInImage
            )
        )
    }

    else -> error("Type ${updateRequest.type} not implemented yet")
}

