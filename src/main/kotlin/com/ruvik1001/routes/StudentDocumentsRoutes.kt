package com.ruvik1001.routes

import com.ruvik1001.model.Document
import com.ruvik1001.model.StudentDocumentsService
import com.ruvik1001.model.StudentDocuments
import com.ruvik1001.model.StudentService
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

fun Route.studentDocumentsRoutes(studentDocumentsService: StudentDocumentsService) {
    route("/studentDocuments") {
        get {
            runBlocking {
                val answerForAll = mutableListOf<String>()
                studentDocumentsService.collection.find().forEach { answerForAll.add(it.toJson()) }
                call.respond(answerForAll.toList())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val studentDocument = studentDocumentsService.read(id)
                    if (studentDocument != null) {
                        call.respond(studentDocument)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Document not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        post {
            val studentDocument = call.receive<StudentDocuments>()
            runBlocking {
                val id = studentDocumentsService.create(studentDocument)
                call.respond(HttpStatusCode.Created, id)
            }
        }
        post("/student/{id}") {
            val studentDocument = call.receive<Document>()
            runBlocking {
                val id = studentDocumentsService.create(StudentDocuments(StudentService(studentDocumentsService.database).read(call.parameters["id"]!!)!!, studentDocument.documentTitle, studentDocument.documentContent))
                call.respond(HttpStatusCode.Created, id)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"]
            val studentDocument = call.receive<StudentDocuments>()
            if (id != null) {
                runBlocking {
                    val updated = studentDocumentsService.update(id, studentDocument)
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Document not found")
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
                    val deleted = studentDocumentsService.delete(id)
                    if (deleted != null) {
                        call.respond(HttpStatusCode.OK, deleted.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student Document not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
