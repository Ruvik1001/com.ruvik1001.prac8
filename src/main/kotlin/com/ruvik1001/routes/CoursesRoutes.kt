package com.ruvik1001.routes

import com.ruvik1001.model.CoursesService
import com.ruvik1001.model.Courses
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.coursesRoutes(coursesService: CoursesService) {
    route("/courses") {
        get {
            runBlocking {
                val answerForAll = mutableListOf<String>()
                coursesService.collection.find().forEach { answerForAll.add(it.toJson()) }
                call.respond(answerForAll.toList())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val courses = coursesService.read(id)
                    if (courses != null) {
                        call.respond(courses)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Course not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        post {
            val courses = call.receive<Courses>()
            runBlocking {
                val id = coursesService.create(courses)
                call.respond(HttpStatusCode.Created, id)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"]
            val courses = call.receive<Courses>()
            if (id != null) {
                runBlocking {
                    val updated = coursesService.update(id, courses)
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Course not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        delete("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val deleted = coursesService.delete(id)
                    if (deleted != null) {
                        call.respond(HttpStatusCode.OK, deleted.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Course not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
