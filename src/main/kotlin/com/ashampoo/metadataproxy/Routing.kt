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
                call.respondText(SERVER_BANNER)
                return@post
            }

            val authToken = call.request.header(AUTHORIZATION_HEADER)

            val response = httpClient.get(remoteUrl) {

                /* If set, pass the auth token on to the remote service */
                if (authToken != null)
                    header(AUTHORIZATION_HEADER, authToken)
            }

            /* If the remote URL requires authorization we forward it as is. */
            if (response.status == HttpStatusCode.Unauthorized) {
                call.respond(response.status, response.bodyAsText())
                return@post
            }

            if (!response.status.isSuccess()) {

                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = "Call to remove URL responded with ${response.status}"
                )

                return@post
            }

            val remoteBytes = response.bodyAsBytes()

            // TODO Implement actual logic

            call.respond(HttpStatusCode.OK)
        }
    }
}

