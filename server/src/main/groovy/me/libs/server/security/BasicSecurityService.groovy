package me.libs.server.security

import com.google.inject.Inject
import me.libs.server.persistence.PersistenceService

/**
 * @author Noam Y. Tenne
 */
class BasicSecurityService implements SecurityService {

    @Inject
    PersistenceService persistenceService

    @Override
    boolean login() {
//        persistenceService.get
        return false
    }
}
