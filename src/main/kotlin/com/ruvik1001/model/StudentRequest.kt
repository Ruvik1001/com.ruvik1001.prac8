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
data class Request(
    val data: String
)

//one-to-one
@Serializable
data class StudentRequest(
    @BsonId val student: Student,
    val requestToEnrollInTheProgram: String
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): StudentRequest = json.decodeFromString(document.toJson())
    }
}

class StudentRequestService(val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("student_request")
        collection = database.getCollection("student_request")
    }

    suspend fun create(sr: StudentRequest): String = withContext(Dispatchers.IO) {
        val doc = sr.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun read(id: String): StudentRequest? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(StudentRequest::fromDocument)
    }

    suspend fun update(id: String, sr: StudentRequest): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), sr.toDocument())
    }

    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}
