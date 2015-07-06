package me.libs.server.api.handler

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import me.libs.server.domain.Book
import me.libs.server.persistence.PersistenceService
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
        def persistenceService = Mock(PersistenceService)
        def bookHandler = new BookHandler()
        bookHandler.persistenceService = persistenceService

        when:
        def result = handle(bookHandler) {
            pathBinding(id: '123')
        }

        then:
        1 * persistenceService.getBook('123') >> {
            new Book(id: '123', authors: ['Jim Koogleshreiber', 'John Boochmacher'] as Set,
                    title: 'Necronomicon', isbn: '978-0380751921', cover: 'link/binary')
        }
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
        def persistenceService = Mock(PersistenceService)
        def bookHandler = new BookHandler()
        bookHandler.persistenceService = persistenceService

        def builder = new JsonBuilder()
        builder {
            authors (['Jim Koogleshreiber', 'John Boochmacher'])
            title 'Necronomicon'
            isbn '978-0380751921'
            cover 'link/binary'
        }

        when:
        def result = handle(bookHandler) {
            method 'PUT'
            body(builder.toString(), HttpHeaderConstants.JSON.toString())
        }

        then:
        1 * persistenceService.createBook(_ as Book) >> { Book toSave ->
            assert toSave.authors == ['Jim Koogleshreiber', 'John Boochmacher'] as Set
            assert toSave.title == 'Necronomicon'
            assert toSave.isbn == '978-0380751921'
            assert toSave.cover == 'link/binary'
            toSave.id = 'id'
            toSave
        }
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
        def persistenceService = Mock(PersistenceService)
        def bookHandler = new BookHandler()
        bookHandler.persistenceService = persistenceService

        def builder = new JsonBuilder()
        builder {
            id '123'
            authors (['Jim Koogleshreiber', 'John Boochmacher'])
            title 'Necronomicon'
            isbn '978-0380751921'
            cover 'link/binary'
        }

        when:
        def result = handle(bookHandler) {
            method 'POST'
            pathBinding(id: '123')
            body(builder.toString(), HttpHeaderConstants.JSON.toString())
        }

        then:
        1 * persistenceService.updateBook(_ as Book) >> { Book toSave ->
            assert toSave.id == '123'
            assert toSave.authors == ['Jim Koogleshreiber', 'John Boochmacher'] as Set
            assert toSave.title == 'Necronomicon'
            assert toSave.isbn == '978-0380751921'
            assert toSave.cover == 'link/binary'
        }
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
        def persistenceService = Mock(PersistenceService)
        def bookHandler = new BookHandler()
        bookHandler.persistenceService = persistenceService

        when:
        def result = handle(bookHandler) {
            method 'DELETE'
            pathBinding(id: '123')
        }

        then:
        1 * persistenceService.deleteBook('123')
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
