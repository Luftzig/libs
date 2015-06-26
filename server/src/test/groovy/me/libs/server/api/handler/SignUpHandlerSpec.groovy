package me.libs.server.api.handler

import groovy.json.JsonSlurper
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import me.libs.server.security.UsernameUnavailableException
import ratpack.http.Status
import spock.lang.Specification

import static ratpack.groovy.test.handling.GroovyRequestFixture.handle
import static ratpack.http.Status.OK
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
class SignUpHandlerSpec extends Specification {

    def 'Sign up but send no content'() {
        setup:
        def result = handle(new SignUpHandler()) {
            method 'PUT'
        }

        expect:
        result.status.code == 400
    }

    def 'Sign up with an already existing user'() {
        setup:
        def handler = new SignUpHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService
        UsernameUnavailableException e = new UsernameUnavailableException('jim')

        when:
        def result = handle(handler) {
            method 'PUT'
            body '{"username": "jim", "password": "bob", "email": "jim@jim.com"}', JSON.toString()
        }

        then:
        1 * securityService.signUp('jim', 'jim@jim.com', 'bob') >> { throw e }
        result.status.code == 409
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.errors.first() == e.message
    }

    def 'Sign up and experience an error'() {
        setup:
        def handler = new SignUpHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService

        when:
        def result = handle(handler) {
            method 'PUT'
            body '{"username": "jim", "password": "bob", "email": "jim@jim.com"}', JSON.toString()
        }

        then:
        1 * securityService.signUp('jim', 'jim@jim.com', 'bob') >> { throw new RuntimeException('moo') }
        result.status.code == 500
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.errors.first() == 'moo'
    }

    def 'Sign up'() {
        setup:
        def handler = new SignUpHandler()
        def securityService = Mock(SecurityService)
        handler.securityService = securityService
        def subject = new Subject(username: 'jim', email: 'jim@jim.com')

        when:
        def result = handle(handler) {
            method 'PUT'
            body '{"username": "jim", "password": "bob", "email": "jim@jim.com"}', JSON.toString()
        }

        then:
        1 * securityService.signUp('jim', 'jim@jim.com', 'bob') >> subject
        1 * securityService.getOrCreateApiKey(subject) >> 'apiKey'
        result.status.code == OK.code
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.apiKey == 'apiKey'
    }
}
