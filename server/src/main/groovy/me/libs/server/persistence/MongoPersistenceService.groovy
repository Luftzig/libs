package me.libs.server.persistence

import com.google.inject.Inject
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
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
        "Hello Libs :) - Using Mongo version: ${mongoClient.getDatabase('libs').runCommand(new BasicDBObject('buildInfo', '')).getString('version')}"
    }

    @Override
    boolean usernameAvailable(String username) {
        def libs = mongoClient.getDatabase('libs')
        def subjects = libs.getCollection('subject')
        subjects.count(new Document('username', username)) == 0
    }

    @Override
    void signUp(String username, String email, String hashedPassword) {
        def libs = mongoClient.getDatabase('libs')
        def subjects = libs.getCollection('subject')
        subjects.insertOne(new Document('_id', ObjectId.get().toString()).append('username', username).append('email', email).append('password', hashedPassword))
    }

    @Override
    String getApiKey(String username) {
        def libs = mongoClient.getDatabase('libs')
        def apiKeys = libs.getCollection('apiKey')
        def results = apiKeys.find(new Document('username', username))
        def apiKey = results.first()
        if (apiKey) {
            return apiKey.getString('apiKey')
        }
        null
    }

    @Override
    void setApiKey(String username, String apiKey) {
        def libs = mongoClient.getDatabase('libs')
        def apiKeys = libs.getCollection('apiKey')
        if (apiKeys.count(new Document('username', username)) == 0) {
            apiKeys.insertOne(new Document('_id', ObjectId.get().toString()).append('username', username).append('apiKey', apiKey))
        } else {
            apiKeys.updateOne(new Document('username', username), new Document('$set', new Document('apiKey', apiKey)))
        }
    }

    @Override
    boolean loginApiKey(String username, String apiKey) {
        def libs = mongoClient.getDatabase('libs')
        def apiKeys = libs.getCollection('apiKey')
        apiKeys.count(new Document('username', username).append('apiKey', apiKey)) > 0
    }

    @Override
    boolean login(String username, String hashedPassword) {
        def libs = mongoClient.getDatabase('libs')
        def subjects = libs.getCollection('subject')
        subjects.count(new Document('username', username).append('password', hashedPassword)) > 0
    }
}
