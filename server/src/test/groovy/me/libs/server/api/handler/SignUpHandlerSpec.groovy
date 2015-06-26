package me.libs.server.api.handler

import groovy.json.JsonSlurper
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import me.libs.server.security.UsernameUnavailableException
import spock.lang.Specification

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import static io.netty.handler.codec.http.HttpResponseStatus.CONFLICT
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import static io.netty.handler.codec.http.HttpResponseStatus.OK
import static ratpack.groovy.test.handling.GroovyRequestFixture.handle
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
        result.status.code == BAD_REQUEST.code()
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
        result.status.code == CONFLICT.code()
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
        result.status.code == INTERNAL_SERVER_ERROR.code()
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
        result.status.code == OK.code()
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.apiKey == 'apiKey'
    }
}
