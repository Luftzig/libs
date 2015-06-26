package me.libs.server.api.handler

import groovy.json.JsonSlurper
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import ratpack.http.Status
import spock.lang.Specification

import static io.netty.handler.codec.http.HttpHeaderNames.WWW_AUTHENTICATE
import static ratpack.groovy.test.handling.GroovyRequestFixture.handle
import static ratpack.http.Status.OK
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
class LogInHandlerSpec extends Specification {

    def 'Login with wrong content type'() {
        setup:
        def result = handle(new LogInHandler()) {
            method 'POST'
        }

        expect:
        result.status.code == 400
    }

    def 'Login empty credentials'() {
        setup:
        def handler = new LogInHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService

        when:
        def result = handle(handler) {
            method 'POST'
            body '{"username": "", "password": ""}', JSON.toString()
        }

        then:
        1 * securityService.login('', '') >> Subject.ANYONYMOUS
        result.status.code == 401
        result.headers.get(WWW_AUTHENTICATE) == 'Basic realm="libs-api"'
    }

    def 'Login wrong credentials'() {
        setup:
        def handler = new LogInHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService

        when:
        def result = handle(handler) {
            method 'POST'
            body '{"username": "jim", "password": "bob"}', JSON.toString()
        }

        then:
        1 * securityService.login('jim', 'bob') >> Subject.ANYONYMOUS
        result.status.code == 401
        result.headers.get(WWW_AUTHENTICATE) == 'Basic realm="libs-api"'
    }

    def 'Login and have the subject lookup fail'() {
        setup:
        def handler = new LogInHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService

        when:
        def result = handle(handler) {
            method 'POST'
            body '{"username": "jim", "password": "bob"}', JSON.toString()
        }

        then:
        1 * securityService.login('jim', 'bob') >> { throw new RuntimeException('moo') }
        result.status.code == 500
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.errors.first() == 'moo'
    }

    def 'Login and have the API key lookup fail'() {
        setup:
        def handler = new LogInHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService
        def subject = new Subject(username: 'jim')

        when:
        def result = handle(handler) {
            method 'POST'
            body '{"username": "jim", "password": "bob"}', JSON.toString()
        }

        then:
        1 * securityService.login('jim', 'bob') >> subject
        1 * securityService.getOrCreateApiKey(subject) >> { throw new RuntimeException('moo') }

        result.status.code == 500
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.errors.first() == 'moo'
    }

    def 'Login'() {
        setup:
        def handler = new LogInHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService
        def subject = new Subject(username: 'jim')

        when:
        def result = handle(handler) {
            method 'POST'
            body '{"username": "jim", "password": "bob"}', JSON.toString()
        }

        then:
        1 * securityService.login('jim', 'bob') >> subject
        1 * securityService.getOrCreateApiKey(subject) >> 'apiKey'

        result.status.code == OK.code
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.apiKey == 'apiKey'
    }
}
