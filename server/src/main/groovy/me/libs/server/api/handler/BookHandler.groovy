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
class BookHandler implements Handler {

    @Override
    void handle(Context context) throws Exception {
        def response = context.response;
        def pathTokens = context.pathTokens;

        def request = context.request;
        def method = request.method;
        if (pathTokens.containsKey('id')) {
            if (method.get) {
                response.contentType('application/json').send('{ "id": "id", "authors": ["Jim Koogleshreiber", "John Boochmacher"], ' +
                        '"title": "Necronomicon", "isbn (optional)": "978-0380751921", "cover": "link/binary" }');
            } else if (method.post) {
                response.send("Book ${pathTokens.get('id')} has been updated.");
            } else if (method.delete) {
                response.send("Book ${pathTokens.get('id')}  has been removed.");
            }
        } else {
            if (method.put) {
                response.status(201).contentType('application/json').send('{ "id": "id" }');
            }
        }
    }
}
