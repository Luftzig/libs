package me.libs.server.api.handler;

import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.HttpMethod;
import ratpack.http.Request;
import ratpack.http.Response;
import ratpack.path.PathTokens;

/**
 * @author Noam Y. Tenne
 */
public class BookHandler implements Handler {

    @Override
    public void handle(Context context) throws Exception {
        Response response = context.getResponse();
        PathTokens pathTokens = context.getPathTokens();

        Request request = context.getRequest();
        HttpMethod method = request.getMethod();
        if (pathTokens.containsKey("id")) {
            if (method.isGet()) {
                response.contentType("application/json").send("{ \"id\": \"id\", \"authors\": [\"Jim Koogleshreiber\", \"John Boochmacher\"], " +
                        "\"title\": \"Necronomicon\", \"isbn (optional)\": \"978-0380751921\", \"cover\": \"link/binary\" }");
            } else if (method.isPost()) {
                response.send("Book " + pathTokens.get("id") + " has been updated.");
            } else if (method.isDelete()) {
                response.send("Book " + pathTokens.get("id") + " has been removed.");
            }
        } else {
            if (method.isPut()) {
                response.status(201).contentType("application/json").send("{ \"id\": \"id\" }");
            }
        }
    }
}
