package me.libs.server.security

import spock.lang.Specification

/**
 * @author Noam Y. Tenne
 */
class SubjectSpec extends Specification {

    def 'Construct a new subject'() {
        setup:
        def subject = new Subject(username: 'jim', email: 'jim@jim.com')

        expect:
        subject.username == 'jim'
        subject.email == 'jim@jim.com'
    }

    def 'Use the anonymous subject'() {
        expect:
        Subject.ANYONYMOUS.username == 'anonymous'
    }
}
