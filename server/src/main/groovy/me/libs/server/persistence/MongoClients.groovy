package me.libs.server.persistence

import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.mongodb.client.model.IndexOptions

/**
 * @author Noam Y. Tenne
 */
class MongoClients {

    MongoClient get(String uri) {
        def mongoClientURI = new MongoClientURI(uri)
        def mongoClient = new MongoClient(mongoClientURI)
        ensureCollections(mongoClient)
        ensureIndexes(mongoClient)
        mongoClient
    }

    private void ensureCollections(MongoClient mongoClient) {
        def libs = mongoClient.getDatabase('libs')
        if (!libs.getCollection('subject')) {
            libs.createCollection('subject')
        }

        if (!libs.getCollection('apiKey')) {
            libs.createCollection('apiKey')
        }
    }

    private void ensureIndexes(MongoClient mongoClient) {
        def libs = mongoClient.getDatabase('libs')
        def subjects = libs.getCollection('subject')
        if (!subjects.listIndexes().any {it.get('unique_username')}) {
            subjects.createIndex(new BasicDBObject('username', 1), new IndexOptions(unique: true, name: 'unique_username'))
        }
        def apiKeys = libs.getCollection('apiKey')
        if (!apiKeys.listIndexes().any {it.get('unique_username')}) {
            apiKeys.createIndex(new BasicDBObject('username', 1), new IndexOptions(unique: true, name: 'unique_username'))
        }
    }
}
