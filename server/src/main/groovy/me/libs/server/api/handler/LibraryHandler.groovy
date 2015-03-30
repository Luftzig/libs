package me.libs.server.api.handler

import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.HttpMethod
import ratpack.http.Request
import ratpack.http.Response
import ratpack.path.PathTokens

/**
 * @author Noam Y. Tenne
 */
class LibraryHandler implements Handler {
    @Override
    void handle(Context context) throws Exception {
        Response response = context.getResponse();
        PathTokens pathTokens = context.getPathTokens();

        Request request = context.getRequest();
        HttpMethod method = request.getMethod();
        if (pathTokens.containsKey("library")) {
            if (method.isGet()) {
                response.contentType("application/json").send("{ \"name\": \"library\",\"geoLocation\": \"point\"}");
            } else if (method.isPut()) {
                response.status(200).send("Library " + pathTokens.get("library") + " has been created.");
            } else if (method.isPost()) {
                response.status(200).send("Library " + pathTokens.get("library") + " has been updated.");
            } else if (method.isDelete()) {
                response.send("Library " + pathTokens.get("library") + " has been removed.");
            }
        } else {
            if (method.isGet()) {
                response.contentType("application/json").send("[{ \"name\": \"library1\",\"geoLocation\": \"point\"}," +
                        "{\"name\": \"library2\",\"geoLocation\": \"point\"}]");
            }
        }
    }
}
