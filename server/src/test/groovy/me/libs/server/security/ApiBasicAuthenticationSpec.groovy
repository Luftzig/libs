package me.libs.server.security

import ratpack.http.Headers
import spock.lang.Specification

import static io.netty.handler.codec.http.HttpHeaderNames.AUTHORIZATION

/**
 * @author Noam Y. Tenne
 */
class ApiBasicAuthenticationSpec extends Specification {

    def 'Resolve with no authorization headers'() {
        setup:
        def headers = Stub(Headers) {
            contains(AUTHORIZATION) >> false
        }

        when:
        def subject = new ApiBasicAuthentication(null).resolve(headers)

        then:
        subject == Subject.ANYONYMOUS
    }

    def 'Resolve with non-basic authorization headers'() {
        setup:
        def headers = Stub(Headers) {
            contains(AUTHORIZATION) >> true
            get(AUTHORIZATION) >> 'NTLM jiomadasdas'
        }

        when:
        def subject = new ApiBasicAuthentication(null).resolve(headers)

        then:
        subject == Subject.ANYONYMOUS
    }

    def 'Resolve with non-key-value basic authorization headers'() {
        setup:
        def headers = Stub(Headers) {
            contains(AUTHORIZATION) >> true
            get(AUTHORIZATION) >> "Basic ${'moo'.bytes.encodeBase64()}"
        }

        when:
        def subject = new ApiBasicAuthentication(null).resolve(headers)

        then:
        subject == Subject.ANYONYMOUS
    }

    def 'Resolve with basic authorization headers'() {
        setup:
        def securityService = Mock(SecurityService)
        def headers = Stub(Headers) {
            contains(AUTHORIZATION) >> true
            get(AUTHORIZATION) >> "Basic ${'username:apiKey'.bytes.encodeBase64()}"
        }
        def subjectToReturn = new Subject(username: 'jim', email: 'jim@jim.com')

        when:
        def subject = new ApiBasicAuthentication(securityService).resolve(headers)

        then:
        1 * securityService.loginApiKey('username', 'apiKey') >> subjectToReturn
        subject == subjectToReturn
    }
}
