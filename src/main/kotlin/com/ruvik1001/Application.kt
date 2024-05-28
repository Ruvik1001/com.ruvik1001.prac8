package com.ruvik1001

import com.ruvik1001.model.CoursesService
import com.ruvik1001.model.StudentDocumentsService
import com.ruvik1001.model.StudentRequestService
import com.ruvik1001.model.StudentService
import com.ruvik1001.model.StudentsCoursesService
import com.ruvik1001.database.*
import com.ruvik1001.routes.coursesRoutes
import com.ruvik1001.routes.studentDocumentsRoutes
import com.ruvik1001.routes.studentRequestRoutes
import com.ruvik1001.routes.studentRoutes
import com.ruvik1001.routes.studentsCoursesRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(CIO, port = 8080, host = "127.0.0.21", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    val db = connectToMongoDB()

    val studentService = StudentService(db)
    val coursesService = CoursesService(db)
    val studentRequestService = StudentRequestService(db)
    val studentsCoursesService = StudentsCoursesService(db)
    val studentDocumentsService = StudentDocumentsService(db)

    routing {
        studentRoutes(studentService)
        coursesRoutes(coursesService)
        studentRequestRoutes(studentRequestService)
        studentsCoursesRoutes(studentsCoursesService)
        studentDocumentsRoutes(studentDocumentsService)
    }
}
