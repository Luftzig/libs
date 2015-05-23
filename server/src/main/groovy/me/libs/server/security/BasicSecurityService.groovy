package me.libs.server.security

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.google.inject.Inject
import me.libs.server.persistence.PersistenceService

/**
 * @author Noam Y. Tenne
 */
class BasicSecurityService implements SecurityService {

    @Inject
    PersistenceService persistenceService

    @Override
    Subject login(String username, String password) {
        if (persistenceService.login(username, password)) {
            return new Subject(username: username)
        }
        Subject.ANYONYMOUS
    }

    @Override
    Subject signUp(String username, String email, String password) {
        if (!persistenceService.usernameAvailable(username)) {
            throw new UsernameUnavailableException(username)
        }

        def hashedPassword = hash(password)
        persistenceService.signUp(username, email, hashedPassword)
        new Subject(username: username, email: email)
    }

    @Override
    String getOrCreateApiKey(Subject subject) {
        def apiKey = persistenceService.getApiKey(subject.username)
        if (apiKey) {
            return apiKey
        }
        apiKey = createNewKey()
        persistenceService.setApiKey(subject.username, apiKey)
        apiKey
    }

    @Override
    boolean loginApiKey(String username, String apiKey) {
        persistenceService.loginApiKey(username, apiKey)
    }

    private String createNewKey() {
        UUID.randomUUID().toString()
    }

    private String hash(String password) {
        def hashedUuid = Hashing.sha512().hashString(password, Charsets.UTF_8)
        hashedUuid.toString()
    }
}
