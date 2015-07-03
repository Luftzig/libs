package me.libs.server.api.handler

import com.google.inject.Inject
import com.google.inject.Singleton
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import me.libs.server.api.Responses
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler
import ratpack.http.Status

import static io.netty.handler.codec.http.HttpHeaderNames.WWW_AUTHENTICATE
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
@Slf4j
@Singleton
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
                    reportAndRespondError(context, username, t)
                    return
                }

                if (subject == Subject.ANYONYMOUS) {
                    def response = response.status(401)
                    response.headers.add(WWW_AUTHENTICATE, 'Basic realm="libs-api"')
                    response.send()
                    return
                }

                try {
                    def apiKey = securityService.getOrCreateApiKey(subject)
                    response.contentType(JSON).status(Status.OK).send("{\"apiKey\": \"$apiKey\"}")
                } catch (Throwable t) {
                    reportAndRespondError(context, username, t)
                }

            }
        }
    }

    private void reportAndRespondError(GroovyContext context, String username, Throwable t) {
        log.error("An exception occurred while trying to log in user ${username}", t)
        Responses.internalError(context, t)
    }
}
