package com.ruvik1001.model

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.types.ObjectId

data class SC(
    val s: String,
    val c: String
)

//may-to-many
@Serializable
data class StudentsCourses(
    val student: Student,
    val courses: Courses
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): StudentsCourses = json.decodeFromString(document.toJson())
    }
}

class StudentsCoursesService(val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("student_courses")
        collection = database.getCollection("student_courses")
    }

    suspend fun create(sc: StudentsCourses): String = withContext(Dispatchers.IO) {
        val doc = sc.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun read(id: String): StudentsCourses? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(StudentsCourses::fromDocument)
    }

    suspend fun update(id: String, sc: StudentsCourses): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), sc.toDocument())
    }

    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}