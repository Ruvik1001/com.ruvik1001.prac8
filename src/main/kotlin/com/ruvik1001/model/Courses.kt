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
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Courses(
    @BsonId val courseCode: String,
    val name: String,
    val department: String,
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Courses = json.decodeFromString(document.toJson())
    }
}


class CoursesService(private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("courses")
        collection = database.getCollection("courses")
    }

    suspend fun create(courses: Courses): String = withContext(Dispatchers.IO) {
        val doc = courses.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun read(id: String): Courses? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Courses::fromDocument)
    }

    suspend fun update(id: String, courses: Courses): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), courses.toDocument())
    }

    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}