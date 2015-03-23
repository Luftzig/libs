package me.libs.server.api.handler;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.HttpMethod;
import ratpack.http.Request;
import ratpack.http.Response;

/**
 * @author Noam Y. Tenne
 */
public class SignUpHandler implements Handler {

    @Override
    public void handle(Context context) throws Exception {
        Request request = context.getRequest();
        HttpMethod method = request.getMethod();
        Response response = context.getResponse();
        if (method.isPut()) {
            response.contentType("application/json").send("{\"apiKey:\": \"blerg\"}");
        } else {
            context.next();
        }
    }
}