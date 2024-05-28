package com.ruvik1001.routes

import com.ruvik1001.model.CoursesService
import com.ruvik1001.model.SC
import com.ruvik1001.model.StudentService
import com.ruvik1001.model.StudentsCoursesService
import com.ruvik1001.model.StudentsCourses
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

fun Route.studentsCoursesRoutes(studentsCoursesService: StudentsCoursesService) {
    route("/studentsCourses") {
        get {
            runBlocking {
                val answerForAll = mutableListOf<String>()
                studentsCoursesService.collection.find().forEach { answerForAll.add(it.toJson()) }
                call.respond(answerForAll.toList())
            }
        }
        get("/{id}") {
            val id = call.parameters["id"]
            if (id != null) {
                runBlocking {
                    val studentsCourses = studentsCoursesService.read(id)
                    if (studentsCourses != null) {
                        call.respond(studentsCourses)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student-Course relation not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
        post {
            val studentsCourses = call.receive<StudentsCourses>()
            runBlocking {
                val id = studentsCoursesService.create(studentsCourses)
                call.respond(HttpStatusCode.Created, id)
            }
        }
        post("/{user_id}/{document_id}") {
            val user_id = call.parameters["user_id"]
            val document_id = call.parameters["document_id"]
            runBlocking {
                val id = studentsCoursesService.create(StudentsCourses(StudentService(studentsCoursesService.database).read(user_id!!)!!, CoursesService(studentsCoursesService.database).read(document_id!!)!!))
                call.respond(HttpStatusCode.Created, id)
            }
        }
        put("/{id}") {
            val id = call.parameters["id"]
            val studentsCourses = call.receive<StudentsCourses>()
            if (id != null) {
                runBlocking {
                    val updated = studentsCoursesService.update(id, studentsCourses)
                    if (updated != null) {
                        call.respond(HttpStatusCode.OK, updated.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student-Course relation not found")
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
                    val deleted = studentsCoursesService.delete(id)
                    if (deleted != null) {
                        call.respond(HttpStatusCode.OK, deleted.toJson())
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Student-Course relation not found")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
