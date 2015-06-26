package me.libs.server.api

import ratpack.groovy.handling.GroovyContext

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
class Responses {
    static boolean noIdSpecified(GroovyContext groovyContext) {
        !groovyContext.pathTokens.containsKey('id')
    }

    static void missingId(GroovyContext groovyContext) {
        groovyContext.response.status(BAD_REQUEST).contentType(JSON).send("{\"errors:\": [\"This command requires an item identifier\"]}")
    }

    static boolean contentIsntJson(GroovyContext groovyContext) {
        !groovyContext.request.body.contentType.json
    }

    static void wrongContent(GroovyContext groovyContext) {
        groovyContext.response.status(BAD_REQUEST).contentType(JSON).send("{\"errors:\": [\"This command requires a JSON body\"]}")
    }

    static void internalError(GroovyContext groovyContext, Throwable t) {
        groovyContext.response.status(INTERNAL_SERVER_ERROR).contentType(JSON).send("{\"errors\": [\"${t.message}\"]}")
    }
}
