package me.libs.server.security

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import me.libs.server.persistence.PersistenceService
import spock.lang.Specification

/**
 * @author Noam Y. Tenne
 */
class BasicSecurityServiceSpec extends Specification {

    def 'Login with valid credentials'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def subject = service.login('jim', 'bob')

        then:
        1 * persistenceService.login('jim', hashPassword('bob')) >> true
        subject.username == 'jim'
    }

    def 'Login with invalid credentials'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def subject = service.login('jim', 'bob')

        then:
        1 * persistenceService.login('jim', hashPassword('bob')) >> false
        subject == Subject.ANYONYMOUS
    }

    def 'Sign up with unavailable username'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        service.signUp('jim', 'jim@jim.com', 'bob')

        then:
        persistenceService.usernameAvailable('jim') >> false

        then:
        def e = thrown(UsernameUnavailableException)
        e.message == '\'jim\' is already taken'
    }

    def 'Sign up'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def subject = service.signUp('jim', 'jim@jim.com', 'bob')

        then:
        1 * persistenceService.usernameAvailable('jim') >> true
        1 * persistenceService.signUp('jim', 'jim@jim.com', hashPassword('bob'))
        subject.username == 'jim'
        subject.email == 'jim@jim.com'
    }

    def 'Get an existing API key'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def apiKey = service.getOrCreateApiKey(new Subject(username: 'jim'))

        then:
        1 * persistenceService.getApiKey('jim') >> 'apiKey'
        apiKey == 'apiKey'
    }

    def 'Create a new API key'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def apiKey = service.getOrCreateApiKey(new Subject(username: 'jim'))

        then:
        1 * persistenceService.getApiKey('jim') >> null
        1 * persistenceService.setApiKey('jim', _ as String)
        apiKey
    }

    def 'Login with an API key'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def subject = service.loginApiKey('jim', 'apiKey')

        then:
        1 * persistenceService.loginApiKey('jim', 'apiKey') >> true
        subject.username == 'jim'
    }

    def 'Login with an incorrect API key'() {
        setup:
        def persistenceService = Mock(PersistenceService)
        def service = new BasicSecurityService(persistenceService: persistenceService)

        when:
        def subject = service.loginApiKey('jim', 'apiKey')

        then:
        1 * persistenceService.loginApiKey('jim', 'apiKey') >> false
        subject == Subject.ANYONYMOUS
    }

    private String hashPassword(String clearText) {
        Hashing.sha512().hashString(clearText, Charsets.UTF_8)
    }
}
