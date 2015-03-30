import me.libs.server.api.handler.BookHandler
import me.libs.server.api.handler.LibraryHandler
import me.libs.server.api.handler.LibraryOperationsHandler
import me.libs.server.api.handler.LogInHandler
import me.libs.server.api.handler.SignUpHandler

import static ratpack.groovy.Groovy.ratpack

ratpack {

    handlers {
        get('hello') {
            render 'Hello Libs :)'
        }
        prefix('api/v1') {
            prefix('auth') {
                handler('signup', new SignUpHandler())
                handler('login', new LogInHandler())
            }
            prefix('library') {
                handler(':user/:library?', new LibraryHandler());
                handler(':user/:library/book/:id?', new LibraryOperationsHandler());
            }
            handler('book/:id?', new BookHandler())
        }
    }

}