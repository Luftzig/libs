package me.libs.server.api.handler

import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static io.netty.handler.codec.http.HttpResponseStatus.*
import static me.libs.server.api.Responses.*
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
class LibraryHandler extends GroovyHandler {

    @Override
    protected void handle(GroovyContext context) {
        context.byMethod {
            get {
                libraries(context)
            }
            put {
                addLibrary(context)
            }
            post {
                updateLibrary(context)
            }
            delete {
                deleteLibrary(context)
            }
        }
    }

    private void libraries(GroovyContext groovyContext) {
        if (groovyContext.pathTokens.containsKey('library')) {
            groovyContext.response.contentType(JSON).status(OK).send("{ \"name\": \"${groovyContext.pathTokens.library}\",\"geoLocation\": \"point\"}");
        } else {
            groovyContext.response.contentType(JSON).status(OK).send("[{ \"name\": \"library1\",\"geoLocation\": \"point\"}," +
                    "{\"name\": \"library2\",\"geoLocation\": \"point\"}]");
        }
    }

    private void addLibrary(GroovyContext groovyContext) {
        if (contentIsntJson(groovyContext)) {
            wrongContent(groovyContext)
            return
        }
        groovyContext.response.status(CREATED).contentType(JSON).send('{ "id": "id" }')
    }

    private void updateLibrary(GroovyContext groovyContext) {
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

    private void deleteLibrary(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        groovyContext.response.status(ACCEPTED).send()
    }
}
