package me.libs.server.api.handler

import com.fasterxml.jackson.databind.JsonNode
import com.google.inject.Inject
import groovy.util.logging.Slf4j
import me.libs.server.security.SecurityService
import me.libs.server.security.Subject
import ratpack.handling.Context
import ratpack.handling.Handler

/**
 * @author Noam Y. Tenne
 */
@Slf4j
class LogInHandler implements Handler {

    @Inject
    SecurityService securityService

    @Override
    void handle(Context context) throws Exception {
        def method = context.request.method;
        if (method.post) {
            JsonNode node = context.parse(jsonNode())
            def username = node.get('username').asText()
            def password = node.get('password').asText()
            def subject = securityService.login(username, password)
            if (subject == Subject.ANYONYMOUS) {
                context.response.contentType('application/json').send("{\"apiKey\": \"\"}");
            } else {
                try {
                    def apiKey = securityService.getOrCreateApiKey(subject)
                    context.response.contentType('application/json').send("{\"apiKey\": \"$apiKey\"}");
                } catch (Exception e) {
                    log.error("An exception occurred while trying to log in user ${username}", e)
                    context.response.status(500).contentType('application/json').send("{\"errors:\": [\"${e.message}\"]")
                }
            }
        }
    }
}
