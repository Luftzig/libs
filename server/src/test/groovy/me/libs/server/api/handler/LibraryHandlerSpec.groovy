package me.libs.server.api.handler

import groovy.json.JsonSlurper
import ratpack.http.internal.HttpHeaderConstants
import spock.lang.Specification

import static io.netty.handler.codec.http.HttpResponseStatus.*
import static ratpack.groovy.test.handling.GroovyRequestFixture.handle

/**
 * @author Noam Y. Tenne
 */
class LibraryHandlerSpec extends Specification {

    def 'Get a library by its name'() {
        setup:
        def result = handle(new LibraryHandler()) {
            pathBinding(library: 'libz')
        }

        expect:
        result.status.code == OK.code()
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.name == 'libz'
        resultBody.geoLocation == 'point'
    }

    def 'Get a library but specify no name'() {
        setup:
        def result = handle(new LibraryHandler()) {}

        expect:
        result.status.code == OK.code()
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.size == 2
    }

    def 'Create a new library'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'PUT'
            body('{}', HttpHeaderConstants.JSON.toString())
        }

        expect:
        result.status.code == CREATED.code()
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.id == 'id'
    }

    def 'Create a new library but send no content'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'PUT'
        }

        expect:
        result.status.code == BAD_REQUEST.code()
    }

    def 'Update an existing library'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'POST'
            pathBinding(id: '123')
            body('{}', HttpHeaderConstants.JSON.toString())
        }

        expect:
        result.status.code == ACCEPTED.code()
    }

    def 'Update an existing library with no ID'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'POST'
        }

        expect:
        result.status.code == BAD_REQUEST.code()
    }

    def 'Update an existing library with no content'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'POST'
            pathBinding(id: '123')
        }

        expect:
        result.status.code == BAD_REQUEST.code()
    }

    def 'Delete an existing library'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'DELETE'
            pathBinding(id: '123')
        }

        expect:
        result.status.code == ACCEPTED.code()
    }

    def 'Delete an existing library with no ID'() {
        setup:
        def result = handle(new LibraryHandler()) {
            method 'DELETE'
        }

        expect:
        result.status.code == BAD_REQUEST.code()
    }
}
