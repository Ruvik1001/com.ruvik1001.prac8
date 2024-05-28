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
data class Student(
    val fcs: String,
    val birthday: String,
    @BsonId val diplomaNumber: String
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): Student = json.decodeFromString(document.toJson())
    }
}


class StudentService(private val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("student")
        collection = database.getCollection("student")
    }

    suspend fun create(student: Student): String = withContext(Dispatchers.IO) {
        val doc = student.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun read(id: String): Student? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(Student::fromDocument)
    }

    suspend fun update(id: String, student: Student): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), student.toDocument())
    }

    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}