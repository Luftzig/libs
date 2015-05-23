package me.libs.server.api.handler

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Inject
import groovy.util.logging.Slf4j
import me.libs.server.security.SecurityService
import me.libs.server.security.UsernameUnavailableException
import ratpack.handling.Context
import ratpack.handling.Handler

/**
 * @author Noam Y. Tenne
 */
@Slf4j
class SignUpHandler implements Handler {

    @Inject
    SecurityService securityService

    @Override
    void handle(Context context) throws Exception {
        def method = context.request.method
        if (method.isPut()) {
            JsonNode node = context.parse(jsonNode())
            def username = node.get('username').asText()
            def email = node.get('email').asText()
            def password = node.get('password').asText()
            try {
                def subject = securityService.signUp(username, email, password)
                def apiKey = securityService.getOrCreateApiKey(subject)
                context.response.status(201).contentType('application/json').send("{\"apiKey:\": \"$apiKey\"")
            } catch (UsernameUnavailableException uue) {
                context.response.status(409).contentType('application/json').send("{\"errors:\": [\"${uue.message}\"]")
            } catch (Exception e) {
                log.error("An exception occurred while trying to sign-up user ${username}", e)
                context.response.status(500).contentType('application/json').send("{\"errors:\": [\"${e.message}\"]")
            }
        }
    }
}
