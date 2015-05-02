import me.libs.server.api.handler.BookHandler
import me.libs.server.api.handler.LibraryHandler
import me.libs.server.api.handler.LibraryOperationsHandler
import me.libs.server.api.handler.LogInHandler
import me.libs.server.api.handler.SignUpHandler
import me.libs.server.persistence.PersistenceService
import me.libs.server.persistence.PersistenceServiceModule

import static ratpack.groovy.Groovy.ratpack

ratpack {

    bindings {
        add new PersistenceServiceModule()
    }

    handlers {
        get('hello') { PersistenceService persistenceService ->
            render persistenceService.sayHello()
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