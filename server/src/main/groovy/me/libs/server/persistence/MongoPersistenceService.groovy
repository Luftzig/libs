package me.libs.server.persistence

import com.google.inject.Inject
import com.mongodb.BasicDBObject
import com.mongodb.MongoClient

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
        return false
    }

    @Override
    void signUp(String username, String email, String hashedPassword) {

    }

    @Override
    String getApiKey(String username) {
        return null
    }

    @Override
    void setApiKey(String username, String apiKey) {

    }

    @Override
    boolean loginApiKey(String username, String password) {
        return false
    }

    @Override
    boolean login(String username, String hashedPassword) {
        return false
    }
}
