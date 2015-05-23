package me.libs.server.security

import com.google.inject.AbstractModule
import com.google.inject.Scopes

/**
 * @author Noam Y. Tenne
 */
class SecurityServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(SecurityService).to(BasicSecurityService).in(Scopes.SINGLETON)
    }
}
