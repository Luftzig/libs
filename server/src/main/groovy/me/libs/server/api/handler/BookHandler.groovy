package me.libs.server.api.handler

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static io.netty.handler.codec.http.HttpResponseStatus.*
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
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
        groovyContext.response.status(CREATED).contentType(JSON).send('{ "id": "id" }')
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
        groovyContext.response.status(ACCEPTED).send()
    }

    private void deleteBook(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        groovyContext.response.status(ACCEPTED).send()
    }

    private boolean noIdSpecified(GroovyContext groovyContext) {
        !groovyContext.pathTokens.containsKey('id')
    }

    private void missingId(GroovyContext groovyContext) {
        groovyContext.response.status(BAD_REQUEST).contentType(JSON).send("{\"errors:\": [\"This command requires a book ID\"]}")
    }

    private boolean contentIsntJson(GroovyContext groovyContext) {
        !groovyContext.request.body.contentType.json
    }

    private void wrongContent(GroovyContext groovyContext) {
        groovyContext.response.status(BAD_REQUEST).contentType(JSON).send("{\"errors:\": [\"This command requires a JSON body\"]}")
    }
}
