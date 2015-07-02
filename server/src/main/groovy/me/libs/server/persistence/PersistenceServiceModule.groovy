package me.libs.server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import com.mongodb.MongoClient

/**
 * @author Noam Y. Tenne
 */
class PersistenceServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        def mongoUri = System.getProperty('mongoUri', null)
        if (mongoUri) {
            def mongoClient = new MongoClients().get(mongoUri)
            bind(MongoClient).toInstance(mongoClient)
            bind(PersistenceService).to(MongoPersistenceService).in(Scopes.SINGLETON)
        } else {
            bind(PersistenceService).to(MemoryPersistenceService).in(Scopes.SINGLETON)
        }
    }
}