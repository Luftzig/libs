package me.libs.server.persistence

import com.google.inject.Inject
import com.mongodb.BasicDBList
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import me.libs.server.domain.Book
import org.bson.Document
import org.bson.types.ObjectId

/**
 * @author Noam Y. Tenne
 */
class MongoPersistenceService implements PersistenceService {

    @Inject
    MongoClient mongoClient

    @Override
    String sayHello() {
        "Hello Libs :) - Using Mongo version: ${libsDb().runCommand(new BasicDBObject('buildInfo', '')).getString('version')}"
    }

    @Override
    boolean usernameAvailable(String username) {
        subjects().count(new Document('username', username)) == 0
    }

    @Override
    void signUp(String username, String email, String hashedPassword) {
        def subject = new Document('_id', ObjectId.get().toString())
                .append('username', username)
                .append('email', email)
                .append('password', hashedPassword)
        subjects().insertOne(subject)
    }

    @Override
    String getApiKey(String username) {
        def results = apiKeys().find(new Document('username', username))
        def apiKey = results.first()
        if (apiKey) {
            return apiKey.getString('apiKey')
        }
        null
    }

    @Override
    void setApiKey(String username, String apiKey) {
        def apiKeys = apiKeys()
        if (apiKeys.count(new Document('username', username)) == 0) {
            apiKeys.insertOne(new Document('_id', ObjectId.get().toString()).append('username', username).append('apiKey', apiKey))
        } else {
            apiKeys.updateOne(new Document('username', username), new Document('$set', new Document('apiKey', apiKey)))
        }
    }

    @Override
    boolean loginApiKey(String username, String apiKey) {
        def loginQuery = new Document('username', username).append('apiKey', apiKey)
        apiKeys().count(loginQuery) > 0
    }

    @Override
    boolean login(String username, String hashedPassword) {
        def loginQuery = new Document('username', username).append('password', hashedPassword)
        subjects().count(loginQuery) > 0
    }

    @Override
    Book getBook(String id) {
        def bookIterable = books().find(new Document('_id', id))
        def book = bookIterable.first()
        if (!book) {
            return null
        }
        new Book(id: id, authors: book.get('authors').toSet(), title: book.getString('title'), isbn: book.getString('isbn'),
                cover: book.getString('cover'))
    }

    @Override
    Book createBook(Book book) {
        book.id = ObjectId.get().toString()
        def document = new Document('_id': book.id)
                .append('authors', book.authors)
                .append('title', book.title)
                .append('isbn', book.isbn)
                .append('cover', book.cover)
        books().insertOne(document)
        book
    }

    @Override
    void deleteBook(String id) {
        books().deleteOne(new Document('_id', id))
    }

    @Override
    void updateBook(Book book) {
        def authors = new BasicDBList()
        authors.addAll(book.authors)
        def document = new Document('_id': book.id)
                .append('authors', authors)
                .append('title', book.title)
                .append('isbn', book.isbn)
                .append('cover', book.cover)
        books().replaceOne(new Document('_id': book.id), document)
        book
    }

    private MongoCollection<Document> subjects() {
        libsDb().getCollection('subject')
    }

    private MongoCollection<Document> apiKeys() {
        libsDb().getCollection('apiKey')
    }

    private MongoCollection<Document> books() {
        libsDb().getCollection('book')
    }

    private MongoDatabase libsDb() {
        mongoClient.getDatabase('libs')
    }
}
