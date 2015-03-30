package me.libs.server.api.handler

import ratpack.handling.Context
import ratpack.handling.Handler

/**
 * @author Noam Y. Tenne
 */
class SignUpHandler implements Handler {
    @Override
    void handle(Context context) throws Exception {
        def method = context.request.method;
        if (method.isPut()) {
            context.response.status(201).contentType('application/json').send('{"apiKey:": "blerg"}');
        }
    }
}
