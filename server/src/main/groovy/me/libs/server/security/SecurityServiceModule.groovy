package me.libs.server.security

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import me.libs.server.persistence.MongoPersistenceService
import me.libs.server.persistence.PersistenceService

/**
 * @author Noam Y. Tenne
 */
class SecurityServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SecurityService).to(BasicSecurityService).in(Scopes.SINGLETON)
    }
}
