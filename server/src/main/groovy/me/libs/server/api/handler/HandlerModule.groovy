package me.libs.server.api.handler

import com.google.inject.AbstractModule

/**
 * @author Noam Y. Tenne
 */
class HandlerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(BookHandler)
        bind(LibraryHandler)
        bind(LibraryOperationsHandler)
        bind(LogInHandler)
        bind(SignUpHandler)
    }
}
