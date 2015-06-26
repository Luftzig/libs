package me.libs.server.api

import ratpack.groovy.handling.GroovyContext

import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
class Responses {
    static boolean noIdSpecified(GroovyContext groovyContext) {
        !groovyContext.pathTokens.containsKey('id')
    }

    static void missingId(GroovyContext groovyContext) {
        groovyContext.response.status(400).contentType(JSON).send("{\"errors:\": [\"This command requires an item identifier\"]}")
    }

    static boolean contentIsntJson(GroovyContext groovyContext) {
        !groovyContext.request.body.contentType.json
    }

    static void wrongContent(GroovyContext groovyContext) {
        groovyContext.response.status(400).contentType(JSON).send("{\"errors:\": [\"This command requires a JSON body\"]}")
    }

    static void internalError(GroovyContext groovyContext, Throwable t) {
        groovyContext.response.status(500).contentType(JSON).send("{\"errors\": [\"${t.message}\"]}")
    }
}
