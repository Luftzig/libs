package me.libs.server.api.handler

import groovy.json.JsonSlurper
import ratpack.http.internal.HttpHeaderConstants
import spock.lang.Specification

import static ratpack.groovy.test.handling.GroovyRequestFixture.handle
import static ratpack.http.Status.OK

/**
 * @author Noam Y. Tenne
 */
class BookHandlerSpec extends Specification {

    def 'Get a book by its ID'() {
        setup:
        def result = handle(new BookHandler()) {
            pathBinding(id: '123')
        }

        expect:
        result.status.code == OK.code
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.id == '123'
        resultBody.authors == ['Jim Koogleshreiber', 'John Boochmacher']
        resultBody.title == 'Necronomicon'
        resultBody.isbn == '978-0380751921'
        resultBody.cover == 'link/binary'
    }

    def 'Get a book but specify no ID'() {
        setup:
        def result = handle(new BookHandler()) {}

        expect:
        result.status.code == 400
    }

    def 'Create a new book'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'PUT'
            body('{}', HttpHeaderConstants.JSON.toString())
        }

        expect:
        result.status.code == 201
        def resultBody = new JsonSlurper().parse(result.bodyBytes)
        resultBody.id == 'id'
    }

    def 'Create a new book but send no content'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'PUT'
        }

        expect:
        result.status.code == 400
    }

    def 'Update an existing book'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'POST'
            pathBinding(id: '123')
            body('{}', HttpHeaderConstants.JSON.toString())
        }

        expect:
        result.status.code == 202
    }

    def 'Update an existing book with no ID'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'POST'
        }

        expect:
        result.status.code == 400
    }

    def 'Update an existing book with no content'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'POST'
            pathBinding(id: '123')
        }

        expect:
        result.status.code == 400
    }

    def 'Delete an existing book'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'DELETE'
            pathBinding(id: '123')
        }

        expect:
        result.status.code == 202
    }

    def 'Delete an existing book with no ID'() {
        setup:
        def result = handle(new BookHandler()) {
            method 'DELETE'
        }

        expect:
        result.status.code == 400
    }
}
