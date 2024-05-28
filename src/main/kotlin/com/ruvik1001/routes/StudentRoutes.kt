package com.ruvik1001.routes

import com.ruvik1001.model.StudentService
import com.ruvik1001.model.Student
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

fun Route.studentRoutes(studentService: StudentService) {
    route("/students") {
        get {
            runBlocking {
                val answerForAll = mutableListOf<String>()
                studentService.collection.find().forEach { answerForAll.add(it.toJson()) }
                call.respond(answerForAll.toList())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val student = studentService.read(id)
                    if (student != null) {
                        call.respond(student)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        post {
            val student = call.receive<Student>()
            runBlocking {
                val id = studentService.create(student)
                call.respond(HttpStatusCode.Created, id)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"]
            val student = call.receive<Student>()
            if (id != null) {
                runBlocking {
                    val updated = studentService.update(id, student)
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student not found")
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
                    val deleted = studentService.delete(id)
                    if (deleted != null) {
                        call.respond(HttpStatusCode.OK, deleted.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
