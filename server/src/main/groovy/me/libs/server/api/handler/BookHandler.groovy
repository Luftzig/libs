package me.libs.server.api.handler

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import me.libs.server.domain.Book
import me.libs.server.persistence.PersistenceService
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static me.libs.server.api.Responses.*
import static ratpack.http.Status.OK
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
@Singleton
class BookHandler extends GroovyHandler {

    @Inject
    PersistenceService persistenceService

    @Override
    void handle(GroovyContext context) {
        context.byMethod {
            get {
                bookById(context)
            }
            put {
                addBook(context)
            }
            post {
                updateBook(context)
            }
            delete {
                deleteBook(context)
            }
        }
    }

    private void bookById(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        Book book = persistenceService.getBook(groovyContext.pathTokens.id)
        if (!book) {
            groovyContext.response.status(404).send()
            return
        }
        groovyContext.response.contentType(JSON).status(OK).send(new JsonBuilder(book).toString())
    }

    private void addBook(GroovyContext groovyContext) {
        if (contentIsntJson(groovyContext)) {
            wrongContent(groovyContext)
            return
        }
        Book book = jsonToBook(groovyContext)
        book = persistenceService.createBook(book)
        groovyContext.response.status(201).contentType(JSON).send(new JsonBuilder([id: book.id]).toString())
    }

    private void updateBook(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        if (contentIsntJson(groovyContext)) {
            wrongContent(groovyContext)
            return
        }
        Book book = jsonToBook(groovyContext)
        persistenceService.updateBook(book)
        groovyContext.response.status(202).send()
    }

    private void deleteBook(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        persistenceService.deleteBook(groovyContext.pathTokens.id)
        groovyContext.response.status(202).send()
    }

    private Book jsonToBook(GroovyContext groovyContext) {
        def bookJson = new JsonSlurper().parse(groovyContext.request.body.bytes)
        new Book(id: bookJson.id, authors: bookJson.authors as Set, title: bookJson.title, isbn: bookJson.isbn, cover: bookJson.cover)
    }
}
