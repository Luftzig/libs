package me.libs.server.api.handler

import com.google.inject.Inject
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import me.libs.server.api.Responses
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.Response

import static io.netty.handler.codec.http.HttpHeaderNames.WWW_AUTHENTICATE
import static io.netty.handler.codec.http.HttpResponseStatus.*
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
@Slf4j
class LogInHandler extends GroovyHandler {

    @Inject
    SecurityService securityService

    @Override
    protected void handle(GroovyContext context) {
        context.byMethod {
            post {
                if (Responses.contentIsntJson(context)) {
                    Responses.wrongContent(context)
                    return
                }
                def jsonBody = new JsonSlurper().parse(request.body.bytes)
                def username = jsonBody.username
                def password = jsonBody.password
                def subject
                try {
                    subject = securityService.login(username, password)
                } catch (Throwable t) {
                    reportAndRespondError(response, username, t)
                    return
                }

                if (subject == Subject.ANYONYMOUS) {
                    def response = response.status(UNAUTHORIZED)
                    response.headers.add(WWW_AUTHENTICATE, 'Basic realm="libs-api"')
                    response.send()
                    return
                }

                try {
                    def apiKey = securityService.getOrCreateApiKey(subject)
                    response.contentType(JSON).status(OK).send("{\"apiKey\": \"$apiKey\"}")
                } catch (Throwable t) {
                    reportAndRespondError(response, username, t)
                }

            }
        }
    }

    private void reportAndRespondError(Response response, String username, Throwable t) {
        log.error("An exception occurred while trying to log in user ${username}", t)
        response.status(INTERNAL_SERVER_ERROR).contentType(JSON).send("{\"errors\": [\"${t.message}\"]}")
    }
}
