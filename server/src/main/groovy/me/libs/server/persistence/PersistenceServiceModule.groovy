package me.libs.server.persistence

import com.google.inject.AbstractModule
import com.google.inject.Scopes

/**
 * @author Noam Y. Tenne
 */
class PersistenceServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PersistenceService).to(MongoPersistenceService).in(Scopes.SINGLETON)
    }
}