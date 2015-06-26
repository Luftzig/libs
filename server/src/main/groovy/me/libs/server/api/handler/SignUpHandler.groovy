package me.libs.server.api.handler

import com.google.inject.Inject
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import me.libs.server.api.Responses
import me.libs.server.security.SecurityService
import me.libs.server.security.UsernameUnavailableException
import ratpack.groovy.handling.GroovyContext
import ratpack.groovy.handling.GroovyHandler

import static io.netty.handler.codec.http.HttpResponseStatus.CONFLICT
import static io.netty.handler.codec.http.HttpResponseStatus.OK
import static ratpack.http.internal.HttpHeaderConstants.JSON

/**
 * @author Noam Y. Tenne
 */
@Slf4j
class SignUpHandler extends GroovyHandler {

    @Inject
    SecurityService securityService

    @Override
    protected void handle(GroovyContext context) {
        context.byMethod {
            put {
                if (Responses.contentIsntJson(context)) {
                    Responses.wrongContent(context)
                    return
                }
                def jsonBody = new JsonSlurper().parse(request.body.bytes)
                def username = jsonBody.username
                def email = jsonBody.email
                def password = jsonBody.password
                try {
                    def subject = securityService.signUp(username, email, password)
                    def apiKey = securityService.getOrCreateApiKey(subject)
                    context.response.status(OK).contentType(JSON).send("{\"apiKey\": \"$apiKey\"}")
                } catch (UsernameUnavailableException uue) {
                    context.response.status(CONFLICT).contentType(JSON).send("{\"errors\": [\"${uue.message}\"]}")
                } catch (Throwable t) {
                    log.error("An exception occurred while trying to sign-up user ${username}", t)
                    Responses.internalError(context, t)
                }
            }
        }
    }
}