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

@Serializable
data class Document(
    val documentTitle: String,
    val documentContent: String
)

//one-to-many
@Serializable
data class StudentDocuments(
    val student: Student,
    val documentTitle: String,
    val documentContent: String
) {
    fun toDocument(): Document = Document.parse(Json.encodeToString(this))

    companion object {
        private val json = Json { ignoreUnknownKeys = true }

        fun fromDocument(document: Document): StudentDocuments = json.decodeFromString(document.toJson())
    }
}

class StudentDocumentsService(val database: MongoDatabase) {
    var collection: MongoCollection<Document>

    init {
        database.createCollection("student_documentation")
        collection = database.getCollection("student_documentation")
    }

    suspend fun create(sd: StudentDocuments): String = withContext(Dispatchers.IO) {
        val doc = sd.toDocument()
        collection.insertOne(doc)
        doc["_id"].toString()
    }

    suspend fun read(id: String): StudentDocuments? = withContext(Dispatchers.IO) {
        collection.find(Filters.eq("_id", ObjectId(id))).first()?.let(StudentDocuments::fromDocument)
    }

    suspend fun update(id: String, sd: StudentDocuments): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndReplace(Filters.eq("_id", ObjectId(id)), sd.toDocument())
    }

    suspend fun delete(id: String): Document? = withContext(Dispatchers.IO) {
        collection.findOneAndDelete(Filters.eq("_id", ObjectId(id)))
    }
}