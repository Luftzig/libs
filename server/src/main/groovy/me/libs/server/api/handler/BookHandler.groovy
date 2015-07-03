package me.libs.server.api.handler

import com.google.inject.Singleton
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
        groovyContext.response.contentType(JSON).status(OK)
                .send("{ \"id\": \"${groovyContext.pathTokens.id}\", \"authors\": [\"Jim Koogleshreiber\", \"John Boochmacher\"], " +
                "\"title\": \"Necronomicon\", \"isbn\": \"978-0380751921\", \"cover\": \"link/binary\" }")
    }

    private void addBook(GroovyContext groovyContext) {
        if (contentIsntJson(groovyContext)) {
            wrongContent(groovyContext)
            return
        }
        groovyContext.response.status(201).contentType(JSON).send('{ "id": "id" }')
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
        groovyContext.response.status(202).send()
    }

    private void deleteBook(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        groovyContext.response.status(202).send()
    }
}
