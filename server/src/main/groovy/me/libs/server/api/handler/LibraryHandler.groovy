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
            groovyContext.response.contentType(JSON).status(OK).send("{ \"name\": \"${groovyContext.pathTokens.library}\",\"geoLocation\": \"point\"}")
        } else {
            groovyContext.response.contentType(JSON).status(OK).send("[{ \"name\": \"library1\",\"geoLocation\": \"point\"}," +
                    "{\"name\": \"library2\",\"geoLocation\": \"point\"}]")
        }
    }

    private void addLibrary(GroovyContext groovyContext) {
        if (contentIsntJson(groovyContext)) {
            wrongContent(groovyContext)
            return
        }
        groovyContext.response.status(201).contentType(JSON).send('{ "id": "id" }')
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
        groovyContext.response.status(202).send()
    }

    private void deleteLibrary(GroovyContext groovyContext) {
        if (noIdSpecified(groovyContext)) {
            missingId(groovyContext)
            return
        }
        groovyContext.response.status(202).send()
    }
}
