package me.libs.server.api.handler

import ratpack.handling.Context
import ratpack.handling.Handler

/**
 * @author Noam Y. Tenne
 */
class LoginHandler implements Handler {
    @Override
    void handle(Context context) throws Exception {
        def method = context.request.method;
        if (method.post) {
            context.response.contentType('application/json').send('{"apiKey:": "blerg2"}');
        }
    }
}
