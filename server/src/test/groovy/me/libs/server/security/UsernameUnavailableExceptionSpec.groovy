package me.libs.server.security

import spock.lang.Specification

/**
 * @author Noam Y. Tenne
 */
class UsernameUnavailableExceptionSpec extends Specification {

    def 'Error message'() {
        expect:
        '\'jim\' is already taken' == new UsernameUnavailableException('jim').message
    }
}
