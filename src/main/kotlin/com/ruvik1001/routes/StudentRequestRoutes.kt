package com.ruvik1001.routes

import com.ruvik1001.model.Request
import com.ruvik1001.model.StudentRequestService
import com.ruvik1001.model.StudentRequest
import com.ruvik1001.model.StudentService
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.Route
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.coroutines.runBlocking

fun Route.studentRequestRoutes(studentRequestService: StudentRequestService) {
    route("/studentRequests") {
        get {
            runBlocking {
                val answerForAll = mutableListOf<String>()
                studentRequestService.collection.find().forEach { answerForAll.add(it.toJson()) }
                call.respond(answerForAll.toList())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val studentRequest = studentRequestService.read(id)
                    if (studentRequest != null) {
                        call.respond(studentRequest)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Request not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        post {
            val studentRequest = call.receive<StudentRequest>()
            runBlocking {
                val id = studentRequestService.create(studentRequest)
                call.respond(HttpStatusCode.Created, id)
            }
        }
        post("/student/{id}") {
            val reqText = call.receive<Request>()
            runBlocking {
                val id = studentRequestService.create(StudentRequest(StudentService(studentRequestService.database).read(call.parameters["id"]!!)!!, reqText.data))
                call.respond(HttpStatusCode.Created, id)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"]
            val studentRequest = call.receive<StudentRequest>()
            if (id != null) {
                runBlocking {
                    val updated = studentRequestService.update(id, studentRequest)
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Request not found")
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
                    val deleted = studentRequestService.delete(id)
                    if (deleted != null) {
                        call.respond(HttpStatusCode.OK, deleted.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Request not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
